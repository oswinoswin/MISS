import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Ackley extends AbstractDoubleProblem {

    /**
     * Constructor
     * Creates a default instance of the Ackley problem
     *
     * @param numberOfVariables Number of variables of the problem
     */
    public Ackley(Integer numberOfVariables) {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(1);
        setNumberOfConstraints(0) ;
        setName("Ackley");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(-32.768);
            upperLimit.add(32.768);
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

        //parameters as recommended here: https://www.sfu.ca/~ssurjano/ackley.html
        double a = 20;
        double b = 0.2;
        double c = 2*Math.PI;

        double sum1 = 0.0;
        double sum2 = 0.0;


        for (int i = 0; i < d - 1; i++) {
            sum1 += x[i]*x[i];
            sum2 += Math.cos(c*x[i]);
        }

        double y = -a*Math.exp(-b*Math.sqrt(sum1/d));
        y = y -  Math.exp(sum2/d);
        y = y + a - Math.exp(1);

        solution.setObjective(0, y);
    }



}