import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.util.JMetalException;

/**
 * Created by Cephea on 19.03.2018.
 */
public class KDTreeSelection<S> implements SelectionOperator<KDTree, S> {
    @Override
    public S execute(KDTree solutionTree) {
        if (null == solutionTree) {
            throw new JMetalException("The solution list is null") ;
//        } else if (solutionTree.isEmpty()) {
//            throw new JMetalException("The solution list is empty") ;
//        }
        return null;
    }
}
