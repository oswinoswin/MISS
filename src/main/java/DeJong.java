import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class DeJong extends AbstractDoubleProblem {

    /**
     * Constructor
     * Creates a default instance of the De Jong problem no 1
     * Problem described here: http://www.pg.gda.pl/~mkwies/dyd/geadocu/fcnfun1.html
     * @param numberOfVariables Number of variables of the problem
     */
    public DeJong(Integer numberOfVariables) {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(1);
        setNumberOfConstraints(0) ;
        setName("DeJong");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(-5.12);
            upperLimit.add(5.12);
        }

        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);
    }

    /** Evaluate() method */
    @Override
    public void evaluate(DoubleSolution solution) {
        int d = getNumberOfVariables() ;
        double x;
        double y = 0;

        for (int i = 0; i < d; i++) {
            x = solution.getVariableValue(i) ;
            y += x*x;
        }

        solution.setObjective(0, y);
    }



}