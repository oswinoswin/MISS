import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.util.JMetalException;

import java.util.List;


public class KDTreeSelection<S> implements SelectionOperator<List<S>, S> {

    private int i = 0;

    private KDTree<S> kdTree;

    @Override
    public S execute(List<S> solutionList) {
        if (kdTree == null){
            kdTree = new KDTree<S>(solutionList, Math.round(Math.log(solutionList.size())));
        }

        if (null == solutionList) {
            throw new JMetalException("The solution list is null") ;
//        } else if (solutionTree.isEmpty()) {
//            throw new JMetalException("The solution list is empty") ;
        }

        return null;
    }
}
