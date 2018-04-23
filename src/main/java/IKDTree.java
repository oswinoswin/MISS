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
    KDNode distanced(KDNode kdNode);//returns the edge node from another subtree
    Solution distanced(Solution solution);
    KDNode distancedWithSteps(KDNode kdNode); //uses limited number of steps

}
