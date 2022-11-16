public class TreeNode {

    TreeNode Lleaf;
    TreeNode Rleaf;
    int bestSplit;
    int[][] exists;
    int [] classes;
    TreeNode(  int bestSplit,int[][] exists,int[] classes){
        Rleaf = null;
        Lleaf = null;
        this.classes = classes;
        this.exists = exists;
        this.bestSplit = bestSplit;
    }

    public int getBestSplit(){
        return bestSplit;
    }
    public int[] getClasses(){
        return classes;
    }
    public  int[][] getExists(){return exists;}

    public void setBestSplit(int bestSplit){
        this.bestSplit = bestSplit;
    }
}
