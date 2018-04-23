import org.uma.jmetal.solution.Solution;

class KDNode {
    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    private Solution solution;
    private KDNode left;
    private KDNode right;
    private int depth;
    private int dimensions;

    private OurSolutionComparator solutionComparator = new OurSolutionComparator();



    public KDNode getLeft() {
        return left;
    }

    public void setLeft(KDNode left) {
        this.left = left;
    }

    public KDNode getRight() {
        return right;
    }

    public void setRight(KDNode right) {
        this.right = right;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

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

    public KDNode findInSubTree(Solution s){
        if(equalSolutions(this.solution, s)){
            return this;
        } else {
            depth = depth % dimensions;
            if (Double.parseDouble(this.solution.getVariableValueString(depth)) > Double.parseDouble(s.getVariableValueString(depth))) {
                if (left != null) {
                    return left.findInSubTree(s);
                } else {
                    return null;
                    //throw new JMetalException("Solution not found") ;
                }
            } else {
                if (right != null) {
                    return right.findInSubTree(s);
                } else {
                    return null;
                    //throw new JMetalException("Solution not found") ;
                }
            }
        }
    }

    public KDNode findParentInSubTree(KDNode node){
        if (this.isLeaf())
            return null;

        if ((this.left != null && this.left == node) ||( this.right != null && this.right == node)){
            return this;
        }
        else {
            Solution s = node.getSolution();
            depth = depth % dimensions;
            if(Double.parseDouble(this.solution.getVariableValueString(depth)) > Double.parseDouble(s.getVariableValueString(depth))){
                if (left != null){
                    return left.findParentInSubTree(node);
                } else {
                    return null;
                    //throw new JMetalException("Solution not found") ;
                }
            } else {
                if (right != null){
                    return right.findParentInSubTree(node);
                } else {
                    return null;
                    //throw new JMetalException("Solution not found") ;
                }
            }

        }
    }
    public KDNode findMin(KDNode rootNode, int dimension){
        if (rootNode == null){
            return null;
        }
        if (rootNode.getDepth()%rootNode.getDimensions() == dimension){
            if (rootNode.getLeft() == null){
                return rootNode;
            }
            return findMin(rootNode.getLeft(), dimension);
        }
        KDNode leftMin = findMin(rootNode.getLeft(), dimension);
        KDNode rightMin = findMin(rootNode.getRight(), dimension);

        solutionComparator.setDepth(dimension);
        if (solutionComparator.compare(leftMin.getSolution(), rootNode.getSolution()) < 0){ // left is smaller
            if (solutionComparator.compare(leftMin.getSolution(), rightMin.getSolution()) < 0){
                return leftMin;
            } else {
                return rightMin;
            }
        } else if (solutionComparator.compare(rootNode.getSolution(), rightMin.getSolution()) < 0) { // root is smaller
            return rootNode;
        }
        return rightMin;
    }

    public KDNode findMin(){
        KDNode prev = this;

        if(prev.left == null){
            return prev;
        }
        KDNode next = prev.left;

        while( next != null){
            prev = next;
            next = prev.left;
        }

        return prev;

        /*if(this.left == null){
            return this;
        }
        return this.left.findMin();*/
    }

    public void removeLeftChild(){
        KDNode leftChild = this.left;

        if(leftChild.isLeaf()){
            this.left = null;
        }
        //case when left has only one child
        if (leftChild.left != null && leftChild.right == null){
            this.left = leftChild.left;

        }
        if (leftChild.right != null && leftChild.left == null){
            this.left = leftChild.right;
        }
        //else find succesor of right
        KDNode next = leftChild.right.findMin();
        this.left = next;

    }

    public void removeRightChild(){
        KDNode rightChild = this.right;

        if(rightChild.isLeaf()){
            this.right = null;
        }
        //case when left has only one child
        if (rightChild.left != null && rightChild.right == null){
            this.left = rightChild.left;

        }
        if (rightChild.right != null && rightChild.left == null){
            this.left = rightChild.right;
        }
        //else find succesor of right
        KDNode next = rightChild.right.findMin();
        this.left = next;

    }



    public void printSubtree(){
        String prefix = "";
        for( int i = 0; i < this.depth; i++){
            prefix = prefix + "*";
        }
        System.out.println(prefix + " " + this.getSolution());
        if(left != null)
            left.printSubtree();
        if(right != null)
            right.printSubtree();

    }

    @Override
    public String toString(){
        return "" + this.solution;
//        String leftString = "";
//        String rightString = "";
//        if (left != null){
//            leftString = left.toString();
//        }
//        if (right != null){
//            rightString = right.toString();
//        }
//        return "&" +  this.solution + "" + leftString + "*" + rightString;
    }

    private boolean equalSolutions(Solution s1, Solution s2){
        for (int i = 0; i < s1.getNumberOfVariables(); i++){
            if (s1.getVariableValue(i) != s2.getVariableValue(i)){
                return false;
            }
        }
        return true;

    }


    public Solution getSolution(){
        return this.solution;
    }

}
