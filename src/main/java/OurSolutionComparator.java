import org.uma.jmetal.solution.Solution;

import java.util.Comparator;


public class OurSolutionComparator implements Comparator<Solution> {
    private int depth = 0;

    public void setDepth(int depth){
        this.depth = depth;
    }

    @Override
    public int compare(Solution o1, Solution o2) {
        return Double.compare(Double.parseDouble(o1.getVariableValueString(depth)), Double.parseDouble(o2.getVariableValueString(depth)));
    }
}
