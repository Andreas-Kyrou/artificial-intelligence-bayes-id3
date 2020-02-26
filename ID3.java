import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ID3 {
    HashMap<String,Integer> hamVocabulary=new HashMap<>();
    HashMap<String,Integer> spamVocabulary=new HashMap<>();
    HashMap<String,Integer> allTypes=new HashMap<>();
    ArrayList<Node> tree=new ArrayList<>();
    static double hamWords,spamwords;
   static double hamMails,spamMails;
    double hc;
    int countHam=0,countSpam=0;

    int[][] matrix=new int[2][2];
    static int realHam,realSpam;

    public void run(double res) throws FileNotFoundException {
        train(res);
        test();
        print();

    }

    private void print() {
        System.out.println("Ham mails: "+countHam+"\nSpam mails: "+countSpam+"\n\n");
    }

    private void train(double res) throws FileNotFoundException {
        ham(res);
        spam(res);
        treeMaker();
    }

    private void ham(double res) throws FileNotFoundException {
        String dir="enron1/ham";
        File file=new File(ID3.class.getProtectionDomain().getCodeSource().getLocation().getPath()+dir);
        File[] allFiles=file.listFiles();
        hamMails=allFiles.length;
        hamWords=0;

        hamMails*=res;
        hamVocabulary=new HashMap<>();
        allTypes=new HashMap<>();


        for(int i=0;i<hamMails;i++){
            Scanner input=new Scanner(allFiles[i]);
            while(input.hasNext()){
                String word=input.next();
                if(hamVocabulary.containsKey(word)){
                    int tmp=hamVocabulary.get(word);
                    hamVocabulary.put(word,tmp+1);
                    allTypes.put(word,tmp+1);
                }else{
                    hamVocabulary.put(word,1);
                    allTypes.put(word,1);
                }
                hamWords++;
            }

        }
    }

    private void spam(double res) throws FileNotFoundException {
        String dir="enron1/spam";
        File file=new File(ID3.class.getProtectionDomain().getCodeSource().getLocation().getPath()+dir);
        File[] allFiles=file.listFiles();
        spamwords=0;
        spamMails=allFiles.length;

        spamMails*=res;
        spamVocabulary=new HashMap<>();

        for(int i=0;i<spamMails;i++) {
            Scanner input = new Scanner(allFiles[i]);
            while (input.hasNext()) {
                String word = input.next();
                if (spamVocabulary.containsKey(word)) {
                    int tmp = spamVocabulary.get(word);
                    spamVocabulary.put(word, tmp + 1);
                } else {
                    spamVocabulary.put(word, 1);
                }

                if (allTypes.containsKey(word)) {
                    int tmp_a = allTypes.get(word);
                    allTypes.put(word, tmp_a + 1);
                } else {
                    allTypes.put(word, 1);
                }

                spamwords++;
            }
        }
    }

    private void treeMaker() {

        tree=new ArrayList<>();

        double informationGain=0;
        boolean check=false;
        Node parent=null,child;
        double pSpam,pHam;
        int i=0;
        pSpam=(-spamMails/(spamMails+hamMails));
        pHam=(hamMails/(hamMails+spamMails));
        hc= pSpam*log2(-pSpam)-pHam*log2(pHam);
        for(String word:allTypes.keySet()){//diatrexoume to hashmap me oles tis leksis p vrikame sta mail
            if(hamVocabulary.containsKey(word)&&spamVocabulary.containsKey(word)){
                double hamValue=hamVocabulary.get(word);
                double spamValue=spamVocabulary.get(word);
                double sumValue=hamValue+spamValue;
                informationGain=hc-(pHam)* entropyForX_equals1(hamValue,spamValue,sumValue)+Math.abs(pSpam)*entropyForX_equals0(hamValue,spamValue);
            }

            if(!check){
                Node root=new Node(word,informationGain);
                parent=root;
                check=true;
                tree.add(root);
            }else{
                child=new Node(word,informationGain,parent);
                parent=child;
                tree.add(child);
            }
        }
    }

    private static double entropyForX_equals0(double hamValue, double spamValue) {
        double pNoSpamX=(spamwords+hamWords-spamValue)/(spamwords+hamWords);
        double pNoHamX=(spamwords+hamWords-hamValue)/(spamwords+hamWords);
        return (pNoSpamX*log2(pNoSpamX))-(pNoHamX*log2(pNoHamX));
    }

    private static double entropyForX_equals1(double hamValue, double spamValue, double sumValue) {
        double pHamX=(-hamValue/sumValue);
        double pSpamX=(spamValue/sumValue);
        return (pHamX*log2(Math.abs(pHamX)))-(pSpamX*log2(pSpamX));
    }

    public static double log2(double num){
        return Math.log(num)/Math.log(2);//idiotita logarithmon gia allagi vasis tou logarithmou
    }

    private int[] test() throws FileNotFoundException {

        int[] result=new int[2];
        for(int i=0;i<2;i++)
            for (int j=0;j<2;j++)
                matrix[i][j]=0;
            realSpam=0;realHam=0;
            countSpam=0;countHam=0;

        String dir = "enron";
        File file = new File(ID3.class.getProtectionDomain().getCodeSource().getLocation().getPath() + dir);
        File[] allFiles = file.listFiles();

        String[] name;
        String n;


        String mailword;
        String maxWord = null;
        for (int i = 0; i < allFiles.length; i++) {

            n=allFiles[i].getName();
            name=n.split("\\.");
            if(name[3].equals("ham"))
                realHam++;
            else
                realSpam++;

            double maxIG = 0.0;
            HashMap<String, Integer> mail = new HashMap<>();
            Scanner input = new Scanner(allFiles[i]);
            while (input.hasNext()) {
                mailword = input.next();
                mail.put(mailword, 1);
            }//gia na apothikevoume olous tous tipous lekseon p exi to mail p tha eleksoume

            for (String word : mail.keySet()) {
                for (int j = 0; j < tree.size(); j++) {
                    if (tree.get(j).getWord().equals(word)) {
                        if (tree.get(i).getIG() > maxIG) {
                            maxIG = tree.get(i).getIG();
                            maxWord = word;
                        }
                    }
                }
            }

            double pHam = 0, pSpam = 0;
            if (hamVocabulary.containsKey(maxWord)) {
                pHam = hamVocabulary.get(maxWord);
                pHam /= hamWords;
            }
            if (spamVocabulary.containsKey(maxWord)) {
                pSpam = spamVocabulary.get(maxWord);
                pSpam /= spamwords;
            }

            if (pHam > pSpam) {
                countHam++;

                if(name[3].equals("ham"))
                    matrix[1][1]++;
                else
                    matrix[0][1]++;

            }else {
                countSpam++;

                if(name[3].equals("spam"))
                    matrix[0][0]++;
                else
                    matrix[1][0]++;

            }
        }

        result[0]=countHam;
        result[1]=countSpam;
        return result;

    }

    public void graph() throws FileNotFoundException {
        double p;
        ArrayList<int[]> results = new ArrayList<>();
        double accurancy,precissionH,precissionS,recallS,recallH,f1;
        double precission,recall;
        int[] mail=new int[2];
        for (int i = 1; i < 11; i++) {
            p = i / 10.0;
            train(p);
            results.add(test());

            System.out.println(matrix[0][0]+"\t"+matrix[0][1]+"\n"+matrix[1][0]+"\t"+matrix[1][1]);

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
