import org.uma.jmetal.solution.Solution;

import java.util.List;
import java.util.Random;

public class KDTree<S extends Solution<?>> implements IKDTree {

    private KDNode root;

    private OurSolutionComparator solutionComparator = new OurSolutionComparator();
    private Random random = new Random();

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

    @Override
    public <S extends Solution<?>> void rebuildTree(List<S> population) {
        this.root = null;
        createTree(population);
    }



    // Remove solution from tree

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


    @Override

    public Solution distanced(Solution solution){
        return distancedToTheEnd(solution, root).getSolution();
    }

    private KDNode distancedToTheEnd(Solution solution, KDNode rootNode){

        if (rootNode == null){
            return root;
        }
        int dim = rootNode.getDepth() % rootNode.getDimensions();
        if (Double.parseDouble(solution.getVariableValueString(dim)) < Double.parseDouble(rootNode.getSolution().getVariableValueString(dim))
                && random >= randomnessFactor){
            if (rootNode.getRight() != null){

                return distancedToTheEnd(solution, rootNode.getRight());
            }
        } else {
            if (rootNode.getLeft() != null){
                return distancedToTheEnd(solution, rootNode.getLeft());
            }
        }
        return rootNode;
    }

    private KDNode distancedRandomStop(Solution solution, KDNode rootNode){
        if (rootNode == null){
            return root;
        }
        int dim = rootNode.getDepth() % rootNode.getDimensions();
        if (Double.parseDouble(solution.getVariableValueString(dim)) < Double.parseDouble(rootNode.getSolution().getVariableValueString(dim))){
            if (rootNode.getRight() != null && random.nextBoolean()){
                return distancedRandomStop(solution, rootNode.getRight());
            }
        } else {
            if (rootNode.getLeft() != null && random.nextBoolean()){
                return distancedRandomStop(solution, rootNode.getLeft());
            }
        }
        return rootNode;
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
