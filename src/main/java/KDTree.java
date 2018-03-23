import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.List;

public class KDTree<S extends Solution<?>> implements IKDTree {

    KDNode root;

    @Override
    public void removeSolution(Solution s) {
        //TODO
    }

    @Override
    public void addSolution(Solution s) {
        if(this.isEmpty()){
            root = new KDNode(s);
        }
        else{
            KDNode toAdd = new KDNode(s);
            this.root.addChild(toAdd);
        }
    }

    @Override
    public Solution getFarSolution(Solution solution) {
        //TODO
        if(this.root == null){
            throw new JMetalException("The solution tree is null") ;
        } else if (this.isEmpty()) {
            throw new JMetalException("The solution tree is empty") ;
        }

        return null;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public <S extends Solution<?>> void createTree(List<S> population) {
        for(Solution el: population){
            KDNode toAdd = new KDNode(el);
            root.addChild(toAdd);
        }

    }

    public KDTree() {
    }

    public KDTree(KDNode root) {
        this.root = root;
    }

}
