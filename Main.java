import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Bayes bayes=new Bayes();
        ID3 id3=new ID3();
        int choice=1;
        int last_choice;
       while(choice!=0) {
            Scanner in = new Scanner(System.in);
            System.out.println("Choose algorithm:\nPress 1 for Bayes.\nPress 2 for ID3.\nPress 3 for graph \nPress 0 for exit.\n");
            choice = in.nextInt();
            if(choice==1) {
                last_choice=1;
                bayes.run();
            }else if(choice==2) {
                last_choice=2;
                id3.run(1.0);
            }else if(choice==3){
                System.out.println("Choose algorithm for graph data:\nPress 1 for Bayes.\nPress 2 for ID3.");
                last_choice=in.nextInt();
                if(last_choice==1)
                    bayes.graph();
                else if(last_choice==2)
                    id3.graph();


            }
            else if(choice==0) {

            } else {
                System.out.println("The number of your choice does not exist.\nChoose algorithm:\nPress 1 for Bayes.\nPress 2 for ID3.\nPress 0 for exit.\n");
                choice = in.nextInt();
            }

        }
    }
}
