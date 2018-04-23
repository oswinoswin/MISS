import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.List;

public class KDTree<S extends Solution<?>> implements IKDTree {

    private KDNode root;
    private OurSolutionComparator solutionComparator = new OurSolutionComparator(0);

    public KDTree() {
    }

    public KDTree(KDNode root) {
        this.root = root;
    }

    // Create tree

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


    // Remove solution from tree
    //TODO
    @Override
    public void removeSolution(Solution s) {

      this.root = removeSolution(this.root, s);
    }

    private KDNode removeSolution(KDNode rootNode, Solution toRemove) {
        if (rootNode == null){
            return null;
        }
        if (equalSolutions(rootNode.getSolution(), toRemove)) {
            if (rootNode.getRight() != null){
                KDNode min = findMin(rootNode.getRight(), rootNode.getDepth() % rootNode.getDimensions());
                rootNode.setSolution(min.getSolution());
                min = removeSolution(rootNode.getRight(), min.getSolution());
                rootNode.setRight(min);
            } else if (rootNode.getLeft() != null){
                KDNode max = findMax(rootNode.getLeft(), rootNode.getDepth() % rootNode.getDimensions());
                System.out.println(rootNode.getDepth() % rootNode.getDimensions());
                rootNode.setSolution(max.getSolution());
                max = removeSolution(rootNode.getLeft(), max.getSolution());
                rootNode.setLeft(max);
            } else {
                return null;
            }
            return rootNode;
        } else {
            if (Double.parseDouble(rootNode.getSolution().getVariableValueString(rootNode.getDepth()%rootNode.getDimensions())) > Double.parseDouble(toRemove.getVariableValueString(rootNode.getDepth()%rootNode.getDimensions()))){
                rootNode.setLeft(removeSolution(rootNode.getLeft(), toRemove));
            } else {
                rootNode.setRight(removeSolution(rootNode.getRight(), toRemove));
            }
        }
        return rootNode;

    }

    // Get distanced node

    @Override
    public Solution distanced(Solution solution){
        return distanced(solution, root).getSolution();
    }

    private KDNode distanced(Solution solution, KDNode rootNode){
        if (rootNode == null){
            return root;
        }
        int dim = rootNode.getDepth() % rootNode.getDimensions();
        if (Double.parseDouble(solution.getVariableValueString(dim)) < Double.parseDouble(rootNode.getSolution().getVariableValueString(dim))){
            if (rootNode.getRight() != null){
                return distanced(solution, rootNode.getRight());
            }
        } else {
            if (rootNode.getLeft() != null){
                return distanced(solution, rootNode.getLeft());
            }
        }
        return rootNode;
    }

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
            if(findHeight(root.getLeft()) < findHeight(root.getRight())){
                return goLeft(root, kdNode.getDepth());
            }
        }
        //node is in root right subtree
        if(Double.parseDouble(root.getSolution().getVariableValueString(0)) < Double.parseDouble(kdNode.getSolution().getVariableValueString(0))){
            return goLeft(root, kdNode.getDepth());
        }
        return goRight(root);
    }

    //finds height of a subtree
    public int findHeight(KDNode node){
        if(node == null){
            return 0;
        }
        else{
            return 1 + Math.max(findHeight(node.getLeft()),findHeight(node.getRight()));
        }
    }

    private KDNode goRight(KDNode node){
        if(node == null){
            return null;
        }
        while (node.getRight() != null){
            node = node.getRight();
        }
        return node;
    }
    private KDNode goRight(KDNode node, int steps){
        if(node == null){
            return null;
        }
        while (node.getRight() != null && steps > 0 ){
            node = node.getRight();
            steps--;
        }
        return node;
    }

    private KDNode goLeft(KDNode node){
        if(node == null){
            return null;
        }
        while (node.getLeft() != null){
            node = node.getLeft();
        }
        return node;
    }

    private KDNode goLeft(KDNode node, int steps){
        if(node == null){
            return null;
        }
        while (node.getLeft() != null && steps > 0 ){
            node = node.getLeft();
            steps--;
        }
        return node;
    }

    public KDNode getRoot(){
        return this.root;
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
            solutionComparator.setDepth(root.getDepth());
        }
        else{
            this.root.addChild(s);
        }
    }

    @Override
    public boolean isEmpty() {
        return root == null;
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
