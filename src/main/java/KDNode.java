import org.uma.jmetal.solution.Solution;

class KDNode {
    Solution solution;
    KDNode left;
    KDNode right;
    int depth;

    public KDNode(Solution solution) {
        this.solution = solution;
        this.left = null;
        this.right = null;
    }

    void addChild(KDNode node){
        //TODO - we need comparator!
    }


    void addLeft(KDNode node){
        this.left = node;
    }

    void addRight(KDNode node){
        this.right = node;
    }

    public boolean isLeaf(){
        return this.right == null && this.left == null;
    }
}
