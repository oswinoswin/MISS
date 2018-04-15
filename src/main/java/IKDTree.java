import org.uma.jmetal.solution.Solution;

import java.util.List;

public interface IKDTree {

    void removeSolution(Solution s);

    void addSolution(Solution s);

    Solution getFarSolution(Solution solution);

    boolean isEmpty();

    <S extends Solution<?>> void createTree(List<S> population);


    KDNode findMin();


    void printTree();

    int findHeight(KDNode node);

    KDNode getRoot();
    KDNode distanced(KDNode kdNode);

}
