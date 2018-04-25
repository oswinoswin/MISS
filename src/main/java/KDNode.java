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

    public Solution getSolution() {
        return this.solution;
    }

    public KDNode(Solution solution, int depth) {
        this.solution = solution;
        this.left = null;
        this.right = null;
        this.depth = depth;
        this.dimensions = solution.getNumberOfVariables();
    }

    public void addChild(Solution s) {
        depth = depth % dimensions;
        if (Double.parseDouble(this.solution.getVariableValueString(depth)) > Double.parseDouble(s.getVariableValueString(depth))) {
            if (left != null) {
                left.addChild(s);
            } else {
                addLeft(new KDNode(s, depth + 1));
            }
        } else {
            if (right != null) {
                right.addChild(s);
            } else {
                addRight(new KDNode(s, depth + 1));
            }
        }
    }

    private void addLeft(KDNode node) {
        this.left = node;
    }

    private void addRight(KDNode node) {
        this.right = node;
    }

    public boolean isLeaf() {
        return this.right == null && this.left == null;
    }

    public KDNode findInSubTree(Solution s) {
        if (equalSolutions(this.solution, s)) {
            return this;
        } else {
            depth = depth % dimensions;
            if (Double.parseDouble(this.solution.getVariableValueString(depth)) > Double.parseDouble(s.getVariableValueString(depth))) {
                if (left != null) {
                    return left.findInSubTree(s);
                } else {
                    return null;
                }
            } else {
                if (right != null) {
                    return right.findInSubTree(s);
                } else {
                    return null;
                }
            }
        }
    }


    public void printSubtree() {
        String prefix = "";
        for (int i = 0; i < this.depth; i++) {
            prefix = prefix + "*";
        }
        System.out.println(prefix + " " + this.getSolution());
        if (left != null)
            left.printSubtree();
        if (right != null)
            right.printSubtree();

    }

    @Override
    public String toString() {
        return this.solution.toString();
    }

    private boolean equalSolutions(Solution s1, Solution s2) {
        for (int i = 0; i < s1.getNumberOfVariables(); i++) {
            if (s1.getVariableValue(i) != s2.getVariableValue(i)) {
                return false;
            }
        }
        return true;
    }
}
