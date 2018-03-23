import org.uma.jmetal.solution.Solution;

import java.util.List;

public class KDTree<S extends Solution<?>> implements IKDTree {

    Node root;

    @Override
    public void removeSolution(Solution s) {
        //TODO
    }

    @Override
    public void addSolution(Solution s) {
        //TODO
    }

    @Override
    public Solution getFarSolution(Solution solution) {
        //TODO
        return null;
    }

    @Override
    public boolean isEmpty() {
        return root != null;
    }

    @Override
    public <S extends Solution<?>> void createTree(List<S> population) {

    }

    class Node {
        Solution solution;
        Node left;
        Node right;
    }

}
