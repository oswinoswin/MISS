import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.RandomSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.*;


@SuppressWarnings("serial")
public class RandomSteadyStateGeneticAlgorithm<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, S> {
    private Comparator<S> comparator;
    private int maxEvaluations;
    private int evaluations;
    private OurCSVWriter writer;

    /**
     * Constructor
     */
    public RandomSteadyStateGeneticAlgorithm(Problem<S> problem, int maxEvaluations, int populationSize,
                                       CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator, OurCSVWriter writer) {
        super(problem);
        setMaxPopulationSize(populationSize);
        this.maxEvaluations = maxEvaluations;

        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.selectionOperator = new RandomSelection<>();

        comparator = new ObjectiveComparator<S>(0);
        this.writer = writer;
    }

    @Override
    protected boolean isStoppingConditionReached() {
        return (evaluations >= maxEvaluations);
    }

    @Override
    protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
        Collections.sort(population, comparator);
        int worstSolutionIndex = population.size() - 1;
        if (comparator.compare(population.get(worstSolutionIndex), offspringPopulation.get(0)) > 0) {
            population.remove(worstSolutionIndex);
            population.add(offspringPopulation.get(0));
        }

        return population;
    }

    @Override
    protected List<S> reproduction(List<S> matingPopulation) {
        List<S> offspringPopulation = new ArrayList<>(1);

        List<S> parents = new ArrayList<>(2);
        parents.add(matingPopulation.get(0));
        parents.add(matingPopulation.get(1));

        List<S> offspring = crossoverOperator.execute(parents);
        mutationOperator.execute(offspring.get(0));

        offspringPopulation.add(offspring.get(0));
        return offspringPopulation;
    }

    @Override
    protected List<S> selection(List<S> population) {
        List<S> matingPopulation = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            S solution = selectionOperator.execute(population);
            matingPopulation.add(solution);
        }
        return matingPopulation;
    }

    @Override
    protected List<S> evaluatePopulation(List<S> population) {
        for (S solution : population) {
            getProblem().evaluate(solution);
        }
        return population;
    }

    @Override
    public S getResult() {
        Collections.sort(getPopulation(), comparator);
        return getPopulation().get(0);
    }

    @Override
    public void initProgress() {
        evaluations = 1;
    }

    @Override
    public void updateProgress() {
        writer.write(evaluations, fitness(), metric());
        if (evaluations % 50 == 0) {
            System.out.println(evaluations + " " + fitness() + " " + metric());
        }
        evaluations++;
    }

    @Override
    public String getName() {
        return "ssGA";
    }

    @Override
    public String getDescription() {
        return "Steady-State Genetic Algorithm";
    }


    private double metric(){
        int size = getProblem().getNumberOfVariables();
        int popSize = getPopulation().size();
        double[] tmp = new double[size];
        double[] std = new double[size];
        Arrays.fill(tmp, 0);
        Arrays.fill(std, 0);
        //dodajemy wszystkie wartości
        for (S s : getPopulation()){
            for (int i=0; i<size; i++){
                tmp[i] += Double.parseDouble(s.getVariableValueString(i));
            }
        }
        //liczymy średnią
        for (int i = 0; i<size; i++){
            tmp[i] /= popSize;
        }
        //obliczamy sume do std
        for (S s : getPopulation()){
            for (int i=0; i<size; i++){
                std[i] += Math.pow(Double.parseDouble(s.getVariableValueString(i)) - tmp[i], 2);
            }
        }
        Arrays.sort(std);
        return Math.sqrt(std[0]);
    }

    private double fitness(){
        Collections.sort(getPopulation(), comparator);
        return getPopulation().get(0).getObjective(0);
    }
}
