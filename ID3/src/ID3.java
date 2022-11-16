import java.util.HashMap;

public class ID3 {

    HashMap<Integer, Integer> posWords;
    HashMap<Integer, Integer> negWords;
    HashMap<Integer, Double> IG;


    public void setHashMap(HashMap<Integer, Integer> posWords, HashMap<Integer, Integer> negWords) {
        this.posWords = posWords;
        this.negWords = negWords;

    }


    //calculate entropy
    public HashMap<Integer, Double> entropies() { //we count entropy for each word
        HashMap<Integer, Double> wordsEntropy = new HashMap<Integer, Double>();
        double entropy = 0;
        int posValue;
        int negValue;

        for (HashMap.Entry<Integer, Integer> entry : posWords.entrySet()) {
            int key = entry.getKey();
            posValue= entry.getValue();
            negValue = negWords.get(key);
            int total = posValue+negValue;

            if (negValue > 0 && posValue > 0) {
                entropy = -(Double.valueOf(posValue)/total)*log2(Double.valueOf(posValue)/total) - (Double.valueOf(negValue)/total)*log2(Double.valueOf(negValue)/total);
            } else  { //if the count of a word is 0, then the entropy is 0
                entropy = 0;
            }

            wordsEntropy.put(key, entropy);
        }

        return wordsEntropy;
    }

    public HashMap<Integer, Double> informationGain() {

        HashMap<Integer, Double> IG = new HashMap<Integer, Double>();
        int posSize = posWords.size();
        int negSize = negWords.size();
        int totalSize = posSize + negSize;
        int key;
        double entrpOfWord, igOfWord;

        double totalEntropy = -(Double.valueOf(posSize)/totalSize)*log2(Double.valueOf(posSize)/totalSize) - (Double.valueOf(negSize)/totalSize)*log2(Double.valueOf(negSize)/totalSize);

        for (HashMap.Entry<Integer, Double> entry : entropies().entrySet()) {
            key = entry.getKey();
            entrpOfWord = entry.getValue();
            igOfWord = totalEntropy - entrpOfWord;

            IG.put(key, igOfWord);
        }

        return IG;
    }

    public int maxIG(HashMap<Integer, Double> IG) { //the attribute with the maximum IG
        int maxKey = Integer.MIN_VALUE;
        double maxValue = Double.MIN_VALUE;

        for (HashMap.Entry<Integer, Double> entry : IG.entrySet()) {
            if ( entry.getValue() > maxValue) {
                maxKey = entry.getKey();
                maxValue = entry.getValue();
            }
        }

        return maxKey;
    }

    public String pureClass(int key) {
        if (negWords.get(key) == 0 && posWords.get(key) > 0) {
            return "positive";
        } else if (negWords.get(key) > 0 && posWords.get(key) == 0) {
            return "negative";
        } else
            return "continue";
    }

    private double log2(double value) {
        return (Math.log(value)/Math.log(2.0));
    }


}
