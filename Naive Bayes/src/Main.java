public class Main {


    public static void main(String args[]) {

        NaiveBayesClassifier b = new NaiveBayesClassifier(600,80);
        b.Train();
        b.Evaluate();
    }
}
