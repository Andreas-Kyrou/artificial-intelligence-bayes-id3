import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;


public class Bayes {
    static HashMap<String,Integer> hamVocabulary=new HashMap<>();
    static HashMap<String,Integer> spamVocabulary=new HashMap<>();
    static HashMap<String,Integer> allTypes=new HashMap<>();
    int hamMails,spamMails;
    int hamwords,spamwords;
    int countSpam,countHam,equalsS,equalsH;
    int plithos, sum;
    int[][] matrix=new int[2][2];
    static int realHam,realSpam;
    public void run() throws FileNotFoundException {
        train();

        print(test());
    }

    private void print(int[] res) {
        System.out.println("Count ham: "+res[0]+"\nCount spam: "+res[1]+"\n\n");
    }

    private void train() throws FileNotFoundException {
        trainHam(1.0);
        trainSpam(1.0);

    }

    private void trainSpam(double perS) throws FileNotFoundException {
        String dirName="enron1/spam";
        spamwords=0;
        spamVocabulary=new HashMap<>();
        File dir;
        dir = new File(Bayes.class.getProtectionDomain().getCodeSource().getLocation().getPath()+dirName);
        File[] mails=dir.listFiles();
        spamMails=mails.length;
        spamMails*=perS;

        for(int i=0;i<spamMails;i++){
            Scanner input=new Scanner(mails[i]);
            while(input.hasNext()){
                String word=input.next();
                if(spamVocabulary.containsKey(word)){
                    int tmp=spamVocabulary.get(word);
                    spamVocabulary.put(word,tmp+1);
                }else{
                    spamVocabulary.put(word,1);
                }
                allTypes.put(word,1);
                spamwords++;
            }

        }

    }

    private void trainHam(double perH) throws FileNotFoundException {
        hamVocabulary=new HashMap<>();
        String dirName1="enron1/ham";
        this.hamwords=0;
        File dir1=new File(Bayes.class.getProtectionDomain().getCodeSource().getLocation().getPath()+dirName1);
        File[] mails1=dir1.listFiles();
        hamMails=mails1.length;
        hamMails*=perH;
        for(int i=0;i<hamMails;i++){
            Scanner input=new Scanner(mails1[i]);
            while(input.hasNext()){
                String word=input.next();
                if(hamVocabulary.containsKey(word)){
                    int tmp=hamVocabulary.get(word);
                    hamVocabulary.put(word,tmp+1);
                }else{
                    hamVocabulary.put(word,1);
                }
                allTypes.put(word,1);
                hamwords++;
            }
        }

    }
    private int[] test() throws FileNotFoundException {
        for(int i=0;i<2;i++)
            for (int j=0;j<2;j++)
                matrix[i][j]=0;
        sum=0;
        int[] results =new int[2];
        String dirName1=Bayes.class.getProtectionDomain().getCodeSource().getLocation().getPath()+"enron";
        File dirH=new File(dirName1);
        Scanner input;
        File[] mailsH=dirH.listFiles();
         realSpam=0;realHam=0;
        equalsH=0;equalsS=0;
        countSpam=0;countHam=0;
        String[] name;
        String mailword;
        String n;
        double proHam=(double)hamMails/(hamMails+spamMails);
        double proSpam=(double)spamMails/(hamMails+spamMails);
        double pHam,pSpam;
        plithos=mailsH.length;
        for(int i=0;i<plithos;i++) {
            pHam=0;pSpam=0;
            HashMap<String,Double> mail=new HashMap<>();
            input = new Scanner(mailsH[i]);
            n=mailsH[i].getName();
            name=n.split("\\.");
            if(name[3].equals("ham"))
                realHam++;
            else
                realSpam++;
            while (input.hasNext()) {
                mailword = input.next();
                mail.put(mailword, 1.0);
            }
            sum+=mail.size();
            for(String word:mail.keySet()){

                if (hamVocabulary.containsKey(word)) {
                    pHam = pHam + Math.log((((double)hamVocabulary.get(word) + 1) / ((double)hamwords + (double)(allTypes.size()))));
                } else {
                    pHam =pHam + Math.log((1 / ((double)hamwords + (double)(allTypes.size()))));
                }
                if (spamVocabulary.containsKey(word)) {
                    pSpam = pSpam + Math.log((((double)spamVocabulary.get(word) + 1) / ((double)spamwords + (double)(allTypes.size()))));
                } else {
                    pSpam = pSpam + Math.log((1 / ((double)spamwords + (double)(allTypes.size()))));
                }

            }
            pHam+=Math.log(proHam);pSpam+=Math.log(proSpam);

            if(pHam>pSpam) {
                countHam++;
                if(name[3].equals("ham"))
                    matrix[1][1]++;
                else
                    matrix[0][1]++;
            }
            else {
                countSpam++;
                if(name[3].equals("spam"))
                    matrix[0][0]++;
                else
                    matrix[1][0]++;
            }
        }
        results[0]=countHam;
        results[1]=countSpam;
        return results;
    }

    public void graph() throws FileNotFoundException {
        double p;
        ArrayList<int[]> results = new ArrayList<>();
        double accurancy,precissionH,precissionS,recallS,recallH,f1;
        double precission,recall;
        int[] mail=new int[2];
        for (int i = 1; i < 11; i++) {
            p = i / 10.0;
            trainHam(p);
            trainSpam(p);
            results.add(test());
            accurancy=(matrix[0][0]+matrix[1][1])/(double)(realHam+realSpam);
            precissionH=matrix[1][1]/(double)(matrix[0][1]+matrix[1][1]);
            precissionS=matrix[0][0]/(double)(matrix[0][0]+matrix[1][0]);
            recallH=matrix[1][1]/(double)(matrix[1][0]+matrix[0][0]);
            recallS=matrix[0][0]/(double)(matrix[0][0]+matrix[1][1]);
            precission=(precissionH+precissionS)/2.0;
            recall=(recallH+recallS)/2.0;
            f1=2*(precission*recall)/(precission+recall);
            System.out.println("Statics for "+i*10.0+"% train datas:\n"+"Accurancy = "+accurancy+".\nAverage Precission= "+precission+".\nAverage Recall= "+recall+".\nF1 score= "+f1+".\n");
        }

    }
}
