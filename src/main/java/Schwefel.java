import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Schwefel extends AbstractDoubleProblem {

    /**
     * Constructor
     * Creates a default instance of the Rosenbrock problem
     *
     * @param numberOfVariables Number of variables of the problem
     */
    public Schwefel(Integer numberOfVariables) {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(1);
        setNumberOfConstraints(0) ;
        setName("Schwefel");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(-500.0);
            upperLimit.add(500.0);
        }

        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);
    }

    /** Evaluate() method */
    @Override
    public void evaluate(DoubleSolution solution) {
        int d = getNumberOfVariables() ;

        double[] x = new double[d] ;

        for (int i = 0; i < d; i++) {
            x[i] = solution.getVariableValue(i) ;
        }

        double sum = 0.0;

        for (int i = 0; i < d - 1; i++) {

            sum += x[i]*Math.sin(Math.sqrt(Math.abs(x[i])));
        }

        double y = 418.9829*d - sum;

        solution.setObjective(0, y);
    }
}
