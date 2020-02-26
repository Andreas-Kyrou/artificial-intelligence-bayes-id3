import java.util.Scanner;

public class Node {
    private String word;
    private double IG;
    Node parent;

    Node(String word,double ig){// Kataskevastis gia ton proto komvo diladi mono gia tin riza
        this.word=word;
        IG=ig;
    }

    Node(String word,double ig,Node parent){//kataskevasti gia tous komvous pedia
        this.word=word;
        IG=ig;
        this.parent=parent;
    }

    String getWord(){
        return word;
    }

    double getIG(){
        return IG;
    }

    Node getParent(){
        return parent;
    }
}
