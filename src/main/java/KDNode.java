import org.uma.jmetal.solution.Solution;

import java.util.Comparator;
import java.util.StringTokenizer;

class KDNode {
    Solution solution;
    KDNode left;
    KDNode right;
    private int depth;
    int dimensions;

    public KDNode(Solution solution, int depth) {
        this.solution = solution;
        this.left = null;
        this.right = null;
        this.depth = depth;
        this.dimensions = solution.getNumberOfVariables();
    }

    public void addChild(Solution s){
        depth = depth % dimensions;
        if(Double.parseDouble(this.solution.getVariableValueString(depth)) > Double.parseDouble(s.getVariableValueString(depth))){
            if (left != null){
                left.addChild(s);
            } else {
                addLeft(new KDNode(s, depth+1));
            }
        } else {
            if (right != null){
                right.addChild(s);
            } else {
                addRight(new KDNode(s, depth+1));
            }
        }
    }

    private void addLeft(KDNode node){
        this.left = node;
    }

    private void addRight(KDNode node){
        this.right = node;
    }

    public boolean isLeaf(){
        return this.right == null && this.left == null;
    }

    @Override
    public String toString(){
        String leftString = "";
        String rightString = "";
        if (left != null){
            leftString = left.toString();
        }
        if (right != null){
            rightString = right.toString();
        }
        return this.solution + "" + leftString + "|||" + rightString;
    }

}
