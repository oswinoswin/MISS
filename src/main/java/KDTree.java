import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.List;

public class KDTree<S extends Solution<?>> implements IKDTree {

    private KDNode root;
    private OurSolutionComparator solutionComparator = new OurSolutionComparator();

    public KDTree() {
    }

    public KDTree(KDNode root) {
        this.root = root;
    }

    // Create tree

    @Override
    public <S extends Solution<?>> void createTree(List<S> population) {
        solutionComparator.setDepth(0);
        population.sort(solutionComparator);
        int median = population.size() / 2;
        addSolution(population.get(median));
        createSubTrees(population.subList(0, median), 1);
        createSubTrees(population.subList(median + 1, population.size()), 1);
    }

    private <S extends Solution<?>> void createSubTrees(List<S> population, int depth) {
        if (population.size() > 2) {
            solutionComparator.setDepth(depth % root.getDimensions());
            population.sort(solutionComparator);
            int median = population.size() / 2;
            addSolution(population.get(median));
            createSubTrees(population.subList(0, median), depth + 1);
            createSubTrees(population.subList(median + 1, population.size()), depth + 1);
        } else {
            for (Solution s : population) {
                addSolution(s);
            }
        }
    }


    // Add solution to tree
    @Override
    public void addSolution(Solution s) {
        if (this.isEmpty()) {
            root = new KDNode(s, 0);
        } else {
            this.root.addChild(s);
        }
    }

    // Remove solution from tree
    //TODO
    @Override
    public void removeSolution(Solution s) {
//        KDNode toRemove = this.findInTree(s);
//        int dim = 0;
//        System.out.println("MIN OF TREE:" + dim + " " + findMin(this.getRoot(), dim));

        this.root = removeSolution(this.root, s);
    }

    private KDNode removeSolution(KDNode rootNode, Solution toRemove) {
        System.out.println("\n\nremoving " + toRemove + " from " + rootNode + "\n\n");
        if (rootNode == null){
            System.out.println("chce usunac cos czego nie ma w drzewie?");
            System.out.println(toRemove);
            return null;
        }
        if (toRemove == null){
            System.out.println("nulllll");
        }
        if (equalSolutions(rootNode.getSolution(), toRemove)) {
            if (rootNode.getRight() != null){
                KDNode min = findMin(rootNode.getRight(), rootNode.getDepth() % rootNode.getDimensions());
                System.out.println("RMIN " + min.getSolution());
                rootNode.setSolution(min.getSolution());
                min = removeSolution(rootNode.getRight(), min.getSolution());
                rootNode.setRight(min);
            } else if (rootNode.getLeft() != null){
                KDNode max = findMax(rootNode.getLeft(), rootNode.getDepth() % rootNode.getDimensions());
                System.out.println(rootNode.getDepth() % rootNode.getDimensions());
                rootNode.setSolution(max.getSolution());
                System.out.println("LMAX " + max.getSolution());
                max = removeSolution(rootNode.getLeft(), max.getSolution());
                rootNode.setLeft(max);
            } else {
                return null;
            }
            System.out.println("REMOVED");
            return rootNode;
        } else {
            if (Double.parseDouble(rootNode.getSolution().getVariableValueString(rootNode.getDepth()%rootNode.getDimensions())) > Double.parseDouble(toRemove.getVariableValueString(rootNode.getDepth()%rootNode.getDimensions()))){
                System.out.println("LEFT " + rootNode);
                rootNode.setLeft(removeSolution(rootNode.getLeft(), toRemove));
            } else {
                System.out.println("RIGHT " + rootNode);
                rootNode.setRight(removeSolution(rootNode.getRight(), toRemove));
            }
        }
        return rootNode;

    }

    // Get distanced node

    @Override
    public KDNode distanced(KDNode kdNode) {
        if (kdNode == null) {
            return null;
        }
        if (kdNode == root) {
            if (findHeight(root.getLeft()) < findHeight(root.getRight())) {
                return goLeft(root);
            }
        }
        //node is in root right subtree
        if (Double.parseDouble(root.getSolution().getVariableValueString(0)) < Double.parseDouble(kdNode.getSolution().getVariableValueString(0))) {
            return goLeft(root);
        }
        return goRight(root);
    }

    @Override
    public KDNode distancedWithSteps(KDNode kdNode) {
        if(kdNode == null){
            return null;
        }
        if(kdNode == root){
            if(findHeight(root.left) < findHeight(root.right)){
                return goLeft(root, kdNode.depth);
            }
        }
        //node is in root right subtree
        if(Double.parseDouble(root.solution.getVariableValueString(0)) < Double.parseDouble(kdNode.solution.getVariableValueString(0))){
            return goLeft(root, kdNode.depth);
        }
        return goRight(root);
    }

    //finds height of a subtree
    public int findHeight(KDNode node){
        if(node == null){
            return 0;
        }
        else{
            return 1 + Math.max(findHeight(node.left),findHeight(node.right));
        }
    }

    private KDNode goRight(KDNode node){
        if(node == null){
            return null;
        }
        while (node.right != null){
            node = node.right;
        }
        return node;
    }
    private KDNode goRight(KDNode node, int steps){
        if(node == null){
            return null;
        }
        while (node.right != null && steps > 0 ){
            node = node.right;
            steps--;
        }
        return node;
    }

    private KDNode goLeft(KDNode node){
        if(node == null){
            return null;
        }
        while (node.left != null){
            node = node.left;
        }
        return node;
    }

    private KDNode goLeft(KDNode node, int steps){
        if(node == null){
            return null;
        }
        while (node.left != null && steps > 0 ){
            node = node.left;
            steps--;
        }
        return node;
    }

    public KDNode getRoot(){
        return this.root;
    }



    @Override
    public void removeSolution(Solution s) {
        KDNode toRemove = this.findInTree(s);
        if ( toRemove == null){
            return;
        }

        if (toRemove == this.root){
            //TODO
        }

        else{
            KDNode parent = this.findParent(toRemove);
            if( parent.right == toRemove){
                parent.removeRightChild();
            }else {
                parent.removeLeftChild();
            }
        }

    // Other functions
    private boolean equalSolutions(Solution solution1, Solution solution2){
        for (int i = 0; i < solution1.getNumberOfVariables(); i++){
            if (solution1.getVariableValue(i) != solution2.getVariableValue(i)){
                return false;
            }
        }
        return true;
    }

    @Override
    public KDNode findInTree(Solution s) {
        return root.findInSubTree(s);
    }


    private KDNode findMin(KDNode rootNode, int dimension) {
        if (rootNode == null) {
            return null;
        }
        if (rootNode.getDepth() % rootNode.getDimensions() == dimension) {
            if (rootNode.getLeft() == null) {
                return rootNode;
            }
            return findMin(rootNode.getLeft(), dimension);
        }
        KDNode leftMin = findMin(rootNode.getLeft(), dimension);
        KDNode rightMin = findMin(rootNode.getRight(), dimension);

        solutionComparator.setDepth(dimension);

        return minNode(rootNode, leftMin, rightMin);
    }

    private KDNode findMax(KDNode rootNode, int dimension) {
        if (rootNode == null) {
            return null;
        }
        if (rootNode.getDepth() % rootNode.getDimensions() == dimension) {
            if (rootNode.getRight() == null) {
                return rootNode;
            }
            return findMax(rootNode.getRight(), dimension);
        }

        KDNode leftMax = findMax(rootNode.getLeft(), dimension);
        KDNode rightMax = findMax(rootNode.getRight(), dimension);

        solutionComparator.setDepth(dimension);

        return maxNode(rootNode, leftMax, rightMax);
    }


    private KDNode minNode(KDNode x, KDNode y, KDNode z){
        KDNode res = x;
        if ( y != null) {
            if (solutionComparator.compare(y.getSolution(), res.getSolution()) < 0) { // y is smaller
                res = y;
            }
        }
        if (z != null) {
            if (solutionComparator.compare(z.getSolution(), res.getSolution()) < 0) { // y is smaller
                res = z;
            }
        }
        return res;
    }


    private KDNode maxNode(KDNode x, KDNode y, KDNode z){
        KDNode res = x;
        if ( y != null) {
            if (solutionComparator.compare(y.getSolution(), res.getSolution()) > 0) { // res is smaller
                res = y;
            }
        }
        if (z != null) {
            if (solutionComparator.compare(z.getSolution(), res.getSolution()) > 0) { // res is smaller
                res = z;
            }
        }
        return res;
    }

    private KDNode traverseTree(Solution s, int depth) {
        return null;
    }

    @Override
    public void addSolution(Solution s) {
        if(this.isEmpty()){
            root = new KDNode(s, 0);
            solutionComparator.setDepth(root.depth);

        }
        else{
            this.root.addChild(s);
        }
    }

    @Override
    public Solution getFarSolution(Solution solution) {
        //TODO
        if(this.root == null){
            throw new JMetalException("The solution tree is null") ;
        } else if (this.isEmpty()) {
            throw new JMetalException("The solution tree is empty") ;
        }

        return null;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    // Getters

    public KDNode getRoot() {
        return this.root;
    }

    // Printing

    public void printTree() {
        this.root.printSubtree();
    }

    @Override
    public String toString() {
        return this.root.toString();
    }

}
