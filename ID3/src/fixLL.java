public class fixLL {
    int[][] fix (int point , int[][] exists, int value01, int[] classesBefore ){
        int count=0;

        for (int i = 0; i < exists.length; i++) {
            if (exists[i][point] == value01) {
                count=count+1;
            }
        }

        int[][] leaf = new int[count][exists[0].length];
        int r=0;

        for (int k = 0; k < exists.length; k++) {
            if (exists[k][point] == value01) {
                for (int j = 0; j < exists[0].length; j++) {
                    leaf[r][j] = exists[k][j];
                }
            }
            r++;
            if(r==count){break;}
        }
        for (int i =0;i<leaf.length;i++){
            leaf[i][point]=-1;
        }
        return  leaf;
    }

    int[] getClasses(int[][] exists,int point,int value01,int[] classesBefore){
        int r =0;
        int count=0;
        for (int i = 0; i < exists.length; i++) {
            if (exists[i][point] == value01) {
                count=count+1;
            }
        }
        int[] classes = new int[count];
        for (int k = 0; k < exists.length; k++) {
            if (exists[k][point] == value01) {
                classes[r] = classesBefore[k];
                r++;
            }
        }
        return classes;
    }
}
