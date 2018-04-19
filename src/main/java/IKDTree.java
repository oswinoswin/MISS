import org.uma.jmetal.solution.Solution;

import java.util.List;

public interface IKDTree {

    void removeSolution(Solution s);

    void addSolution(Solution s);

    boolean isEmpty();

    <S extends Solution<?>> void createTree(List<S> population);

//    KDNode findMin();
//    KDNode findMin(KDNode rootNode, int dimension);

    KDNode findInTree(Solution solution);

    void printTree();

    KDNode getRoot();

    int findHeight(KDNode node);

    KDNode distanced(KDNode kdNode);

}
