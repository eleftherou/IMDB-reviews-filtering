import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class NaiveBayesClassifier {

    private HashMap<Integer, String> vocabulary;

    private int [] postotal0;
    private int [] postotal1;
    private int [] negtotal0;
    private int [] negtotal1;

    private int n;
    private int m;
    private int fileCounter;

    private int posCounter;

    private double posProbability;
    private double negProbability;
    private double entropy;


    public NaiveBayesClassifier(int m,int n) {
       this.n =n;
       this.m=m;
    }


    //TRAINING
    public void Train() {

        vocabulary = new HashMap<Integer, String>(); //to hashmap periexei tin leksi, kai tin thesi stin opoia vrisketai sto arxeio vocabulary
        HashMap<String, Integer> stopWords = new HashMap<String, Integer>();

        int i = 0;
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("imdb.vocab"));
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        while (scanner.hasNextLine()) {
            Scanner reader = new Scanner(scanner.nextLine());
            while (reader.hasNext()) {
                i++;
                String data = reader.next();
                if (i <= n) {
                    stopWords.put(data, i);
                }
                if (i > n && i <= m) {
                    vocabulary.put(i,data);
                }
                if(i == m) {
                    break;
                }
            }
        }

        //diavasma tou .feat
        HashMap<Integer,ArrayList<String>> frequency = new HashMap<Integer, ArrayList<String>>();

        ArrayList<String> words;
        fileCounter = 0;
        BufferedReader reader;
        String line;
        try {
            reader = new BufferedReader(new FileReader("labeledBow.feat"));
            while ((line = reader.readLine()) != null) {
                fileCounter++;
                words = new ArrayList<>();

                String[] values = line.split(" ");

                if (values.length == 0) continue;

                for  ( i = 1; i < values.length; i++ ) {
                    String[] split = values[i].split(":");
                    if (Integer.parseInt(split[0]) > n && Integer.parseInt(split[0]) <= m) //pairnoume mono tis m-n
                        words.add(split[0]);
                }

                frequency.put(fileCounter, words);

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int [][] posVectors = new int[fileCounter/2][m-n];
        int [][] negVectors = new int[fileCounter/2][m-n];

        postotal0 = new int [m-n]; //aytoi oi pinakes metrane to plithos twn 0,1 antisoixa pou emfanizontai se kathe xaraktiristiko, ksexwrista gia thetikes kai arnitikes kritikes
        postotal1 = new int [m-n];
        negtotal0 = new int [m-n];
        negtotal1 = new int [m-n];

        for (int j=0; j < m-n; j++){ //arxikopoihsh pinakwn
            postotal0[j] = 0;
            postotal1[j] = 0;
            negtotal0[j] = 0;
            negtotal1[j] = 0;
        }

        ArrayList<String> temp;

        for (i = 0; i < fileCounter/2; i++) {
            temp = frequency.get(i+1);
            for (int j = 0; j < m-n; j++) {
                if (vocabulary.containsKey(j) && temp.contains(Integer.toString(j)) ) {
                    posVectors[i][j] = 1;
                    postotal1[j]++;
                } else {
                    posVectors[i][j] = 0;
                    postotal0[j]++;
                }
            }
            posCounter++;

        }


        for (i = fileCounter/2; i < fileCounter; i++) {
            temp = frequency.get(i+1); //i+1 to title tou arxeiou
            for (int j = 0; j < m-n; j++) {
                if (vocabulary.containsKey(j) && temp.contains(Integer.toString(j)) ) {
                    negVectors[i-fileCounter/2][j] = 1;
                    negtotal1[j]++;
                } else {
                    negVectors[i-fileCounter/2][j] = 0;
                    negtotal0[j]++;
                }
            }

        }

        HashMap<Integer, Integer> posWords = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> negWords = new HashMap<Integer, Integer>();

        for (int j = 0; j < m-n; j++) { //m-n = length //metrame kathe leksi apo tis 450 pou uparxoun, poses fores emfanizontai sta thetika arxeia kai poses sta arnitika
            int posValue = countWords(posVectors, fileCounter, j);
            int negValue = countWords(negVectors, fileCounter, j);
            posWords.put(j,posValue);
            negWords.put(j,negValue);
        }


        posProbability = posCounter / (double)fileCounter;
        negProbability = 1 - posProbability;
        entropy = - posProbability * (Math.log(posProbability) / Math.log(2)) - (negProbability) * (Math.log(negProbability) / Math.log(2));
    }

    public static int countWords(int[][] words, int fileCounter, int j)
    {
        int sum = 0;
        for (int i = 0; i <fileCounter/2; i++) {
            if (words[i][j] == 1) {
                sum++;
            }
        }
        return sum;
    }


    //TESTING
    public void Evaluate()
    {
        double posPrediction;
        double negPrediction;

        double accuracy;
        double total=0;
        double correct=0;

        int rating;
        BufferedReader reader;
        String line;
        int[] vector = new int [m-n]; //size 450

        try {
            reader = new BufferedReader(new FileReader("labeledBow2.feat"));
            while ((line = reader.readLine()) != null) {

                for (int j = 0; j < m - n; j++)  //arxikopoisi pinaka dianusmatos
                {
                    vector[j] = 0;
                }

                String[] values = line.split(" ");

                if (values.length == 0) continue;

                rating = Integer.parseInt(values[0]); //saving the rating to compare it with the prediction

                //ftiaxnw dianysma gia kathe mia kritiki ksexwrista parallila me to diavasma tou arxeiou
                for (int i = 1; i < values.length; i++)
                {
                    String[] split = values[i].split(":");
                    if (Integer.parseInt(split[0]) > n && Integer.parseInt(split[0]) <= m)
                    {
                        vector[(Integer.parseInt(split[0])) - n - 1] = 1;
                    }
                }

                //POSITIVE PREDICTION
                double prediction = 1;
                for (int i = 0; i < m - n; i++)
                {
                    if (vector[i] == 0)
                    {
                        prediction *= (postotal0[i] + 1) / (double)(posCounter + 2); //typos bayes + LACPLACE

                    } else if (vector[i] == 1) {
                        prediction *= (postotal1[i] + 1) / (double)(posCounter + 2);
                        //System.out.println("postotal1 : " + postotal0[i]);
                    }
                }
                posPrediction = posProbability * prediction; //provlepsi na anikei sta positive reviews


                //NEGATIVE PREDICTION
                prediction = 1;
                for (int i = 0; i < m - n; i++)
                {
                    if (vector[i] == 0)
                    {
                        prediction *= (negtotal0[i] + 1) / (double)(fileCounter - posCounter + 2);
                    } else {
                        prediction *= (negtotal1[i] + 1) / (double)(fileCounter - posCounter + 2);
                    }

                }
                negPrediction = negProbability * prediction; //provlepsi na anikei sta positive reviews

                //COMPARING PREDICTIONS WITH REALITY
                if (posPrediction > negPrediction && rating >= 7)  //sygkrisi provlepsewn
                {
                    //System.out.println("POSITIVE predicted as POSITIVE");
                    total++;
                    correct++;
                }
                else if (posPrediction > negPrediction && rating <= 4)
                {
                    //System.out.println("NEGATIVE predicted as POSITIVE");
                    total++;
                }
                else if (negPrediction > posPrediction && rating <= 4)
                {
                    //System.out.println("NEGATIVE predicted as NEGATIVE");
                    total++;
                    correct++;
                }
                else if (negPrediction > posPrediction && rating >= 7)
                {
                    //System.out.println("POSITIVE predicted as NEGATIVE");
                    total++;
                }
                else {
                    total++;
                }
            }

            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        accuracy = correct / total;

        System.out.println("Accuracy: " + accuracy);

    }

}
