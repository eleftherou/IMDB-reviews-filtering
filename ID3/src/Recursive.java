import java.util.HashMap;

public class Recursive {
    ID3 ig = new ID3();
    int i =0;
    TreeNode addRecursive(int [] classes, HashMap<Integer,String> vocabulary, int[][] exists) {
        int mj = 0;
        int count0=0;
        int count1=0;
        for (int p=0;p<classes.length;p++){
            if (classes[p]==0){count0+=1;}
            if (classes[p]==1){count1+=1;}
        }

        fixLL f = new fixLL();
        System.out.println(i++);
        int bestSplit =ig.maxIG(ig.informationGain()); //id of the word with the max IG
        System.out.println(i++);

        if (isTerminal(classes) ) {
            return new TreeNode( bestSplit,exists,classes);
        }

        if (count1>=count0){mj=1;}
        for(int p=0;p<1000;p++){
            if(exists[0][p]==0||exists[0][p]==1) {
                break;
            }
            for (int y =0;y<classes.length;y++){
                classes[y]=mj;
            }
            return new TreeNode( bestSplit,exists,classes);
        }

        TreeNode vd = new TreeNode(bestSplit, exists, classes);
        int[][] newexists0 = f.fix(bestSplit, vd.getExists(), 0 , vd.getClasses()); //create features
        int[][] newexists1 = f.fix(bestSplit, vd.getExists(), 1 , vd.getClasses());
        int[]   classes0 = f.getClasses(vd.getExists(),bestSplit,0,vd.getClasses());
        int[]   classes1 = f.getClasses(vd.getExists(),bestSplit,0,vd.getClasses());
        if (newexists0.length==0||newexists1.length==0){
            return null;
        }

        //majority value
        for (int i=0;i<2;i++) {
            if(i==0){
                System.out.println(newexists0.length+"new ex 0");

                vd.Lleaf = addRecursive(classes0, vocabulary,newexists0);

            }else{
                System.out.println(newexists1.length+"new ex 1");
                vd.Rleaf = addRecursive(classes1, vocabulary,newexists1);
            }
        }

        return vd;
    }

    boolean isTerminal(int[] classes){
        int x = classes [0];
        for (int i = 0;i<classes.length;i++){
            if(x!=classes[i]){return false;}
        }
        return true;
    }

}
