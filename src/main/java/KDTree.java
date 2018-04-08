import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.List;

public class KDTree<S extends Solution<?>> implements IKDTree {

    private KDNode root;
    private OurSolutionComparator solutionComparator = new OurSolutionComparator();

    @Override
    public void removeSolution(Solution s) {
        KDNode toRemove = this.findInTree(s);
        if ( toRemove == null){
            return;
        }

        if (toRemove == this.root){
            //TODO
        }

        else{
            KDNode parent = this.findParent(toRemove);
            if( parent.right == toRemove){
                parent.removeRightChild();
            }else {
                parent.removeLeftChild();
            }
        }


    }

    public KDNode findInTree(Solution s){
        return root.findInSubTree(s);
    }

    public KDNode findParent(KDNode node){
        if( node == this.root){
            return null;
        }
        else {
            return root.findParentInSubTree(node);
        }
    }



    private KDNode traverseTree(Solution s, int depth){
        return null;
    }

    @Override
    public void addSolution(Solution s) {
        if(this.isEmpty()){
            root = new KDNode(s, 0);
        }
        else{
            this.root.addChild(s);
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

    private <S extends Solution<?>> void createSubTrees(List<S> population, int depth){
        if (population.size() > 2){
            solutionComparator.setDepth(depth);
            population.sort(solutionComparator);
            int median = population.size()/2;
            addSolution(population.get(median));
            createSubTrees(population.subList(0,median), depth+1);
            createSubTrees(population.subList(median+1, population.size()), depth+1);
        }
        else {
            for(Solution s : population){
                addSolution(s);
            }
        }
    }

    @Override
    public <S extends Solution<?>> void createTree(List<S> population) {

        solutionComparator.setDepth(0);
        population.sort(solutionComparator);
        int median = population.size()/2;
        addSolution(population.get(median));
        createSubTrees(population.subList(0,median), 1);
        createSubTrees(population.subList(median+1, population.size()), 1);

    }

    public KDTree() {
    }

    public KDTree(KDNode root) {
        this.root = root;
    }

    @Override
    public String toString(){
        return this.root.toString();
    }


}
