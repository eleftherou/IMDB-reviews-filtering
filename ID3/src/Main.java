import java.io.*;
import java.util.*;


public class Main {

    public static void main(String args[]) throws IOException {

        HashMap<Integer, String> vocabulary = new HashMap<Integer, String>(); //id and word

        int n =100;
        int m = 1000;

        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("imdb.vocab"));
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        int i =0;
        while (scanner.hasNextLine()) {
            Scanner reader = new Scanner(scanner.nextLine());
            while (reader.hasNext()) {
                i++;
                String data = reader.next();
                if (i > n && i <= m) {
                    vocabulary.put(i, data); //the words at imdb.vocab are sorted by frequency, so we get the first m-n words
                }
                if(i == m) {
                    break;
                }
            }
        }

        HashMap<Integer,ArrayList<String>> frequency = new HashMap<Integer, ArrayList<String>>();

        ArrayList<String> words; //every file has an arraylist of words, that contains all words between m-n
        int fileCounter = 0;
        BufferedReader reader;
        String line;
        try {
            reader = new BufferedReader(new FileReader("labeledBow.feat"));
            while ((line = reader.readLine()) != null) {
                fileCounter++; //every line belongs to a different file, so when we change line we increase the number of files
                words = new ArrayList<>();

                String[] values = line.split(" ");

                if (values.length == 0) continue;

                for  ( i = 1; i < values.length; i++ ) {
                    String[] split = values[i].split(":");
                    if (Integer.parseInt(split[0]) > n && Integer.parseInt(split[0]) <= m)
                        words.add(split[0]);
                }

                frequency.put(fileCounter, words);

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        int[][] posVectors = new int[fileCounter/2][m];
        int[][] negVectors = new int[fileCounter/2][m];
        int[][] vectors = new int[fileCounter][m];
        int[] classes = new int[fileCounter];

        ArrayList<String> temp;

        for (i = 0; i < fileCounter/2; i++) { //the first half of the files are the positives, the rest of them are the negatives
            temp = frequency.get(i+1);
            classes [i] = 1;
            for (int j = n; j < m; j++) {
                if (vocabulary.containsKey(j) && temp.contains(Integer.toString(j)) ) {
                    posVectors[i][j] = 1;
                } else {
                    posVectors[i][j] = 0;
                }
                vectors[i][j] = posVectors[i][j];
            }

        }


        for (i = fileCounter/2; i < fileCounter; i++) {
            temp = frequency.get(i+1);
            classes[i] = 0;
            for (int j = n; j < m; j++) {
                if (vocabulary.containsKey(j) && temp.contains(Integer.toString(j)) ) {
                    negVectors[i-fileCounter/2][j] = 1;
                } else {
                    negVectors[i-fileCounter/2][j] = 0;
                }
                vectors[i][j] = negVectors[i-fileCounter/2][j];
            }

        }

        HashMap<Integer, Integer> posWords = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> negWords = new HashMap<Integer, Integer>();

        for (int j = n; j < m; j++) { //for the m-n words, we count how many times a word exists in the positive files and in the negative files
            int posValue = countWords(posVectors, fileCounter, j);
            int negValue = countWords(negVectors, fileCounter, j);
            posWords.put(j, posValue);
            negWords.put(j, negValue);
        }



        ID3 classifier = new ID3();

        classifier.setHashMap(posWords, negWords);
        classifier.entropies();
        classifier.maxIG(classifier.informationGain());

        int bestSplit = classifier.maxIG(classifier.informationGain());;
        TreeNode root = new TreeNode(bestSplit,vectors,classes);
        Recursive r  = new Recursive();
        //r.addRecursive(classes,vocabulary,vectors);


    }

    public static int countWords(int[][] words, int fileCounter, int j) {
        int sum = 0;
        for (int i = 0; i <fileCounter/2; i++) {
           if (words[i][j] == 1) {
               sum++;
           }
        }
        return sum;
    }
}