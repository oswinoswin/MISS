import org.uma.jmetal.solution.Solution;

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

    public void removeLeft(){
        if(this.left.isLeaf()){
            this.left = null;
        }
        //case when left has only one child
        if (this.left.left != null && this.left.right == null){
            this.left = this.left.left;

        }
        if (this.left.right != null && this.left.left == null){
            this.left = this.left.right;
        }
        //else find succesor of right

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

    public Solution getSolution(){
        return this.solution;
    }

}
