import org.uma.jmetal.solution.Solution;

class KDNode {
    Solution solution;
    KDNode left;
    KDNode right;
    int depth;
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

    public KDNode findInSubTree(Solution s){
        if(this.solution == s){
            return this;
        }
        depth = depth % dimensions;
        if(Double.parseDouble(this.solution.getVariableValueString(depth)) > Double.parseDouble(s.getVariableValueString(depth))){
            if (left != null){
                return left.findInSubTree(s);
            } else {
                return null;
                //throw new JMetalException("Solution not found") ;
            }
        } else {
            if (right != null){
                return right.findInSubTree(s);
            } else {
                return null;
                //throw new JMetalException("Solution not found") ;
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

    public Solution getSolution(){
        return this.solution;
    }

}
