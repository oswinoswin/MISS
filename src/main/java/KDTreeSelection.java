import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;


public class KDTreeSelection<S extends Solution<?>>  implements SelectionOperator<KDTree<S >, S> {

    @Override
    public S execute(KDTree<S> skdTree) {
        if(skdTree == null){
            throw new JMetalException("The solution tree is null") ;
        } else if (skdTree.isEmpty()) {
            throw new JMetalException("The solution tree is empty") ;
        }
        //TODO find the best solution
        //Maybe do more selections with kdtree
        return null;
    }
}
