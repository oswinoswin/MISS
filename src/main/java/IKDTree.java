import org.uma.jmetal.solution.Solution;

import java.util.List;

public interface IKDTree {

    void removeSolution(Solution s);

    void addSolution(Solution s);

    boolean isEmpty();

    <S extends Solution<?>> void createTree(List<S> population);
    <S extends Solution<?>> void rebuildTree(List<S> population);

    KDNode findInTree(Solution solution);

    void printTree();

    KDNode getRoot();
    Solution distanced(Solution solution, int type, double randomnessFactor);//returns the edge node from another subtree
}
