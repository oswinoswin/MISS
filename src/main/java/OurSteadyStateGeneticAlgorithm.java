import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.selection.RandomSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.*;
import java.util.logging.Logger;


public class OurSteadyStateGeneticAlgorithm<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, S> {
    private Comparator<S> comparator;
    private int maxEvaluations;
    private int evaluations;
    private IKDTree tree;


    private final static Logger LOGGER = Logger.getLogger(OurSteadyStateGeneticAlgorithm.class.getName());

    /**
     * Constructor
     */
//  public OurSteadyStateGeneticAlgorithm(Problem<S> problem, int maxEvaluations, int populationSize,
//                                        CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
//                                        SelectionOperator<List<S>, S> selectionOperator) {
//    super(problem);
//    setMaxPopulationSize(populationSize);
//    this.maxEvaluations = maxEvaluations;
//
//    this.crossoverOperator = crossoverOperator;
//    this.mutationOperator = mutationOperator;
//    this.selectionOperator = selectionOperator;
//
//    comparator = new ObjectiveComparator<S>(0);
//
//  }
    public OurSteadyStateGeneticAlgorithm(Problem<S> problem, int maxEvaluations, int populationSize,
                                          CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator) {
        super(problem);
        setMaxPopulationSize(populationSize);
        this.maxEvaluations = maxEvaluations;

        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;

        this.selectionOperator = new RandomSelection<>();
        // TODO
        this.tree = new KDTree<>();

        comparator = new ObjectiveComparator<S>(0);

//    LOGGER.setLevel(Level.INFO);
//    LOGGER.info("constructor");

    }


    @Override
    protected boolean isStoppingConditionReached() {
        return (evaluations >= maxEvaluations);
    }

    @Override
    protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
//        System.out.println("replacement");
        Collections.sort(population, comparator);
        int worstSolutionIndex = population.size() - 1;
        if (comparator.compare(population.get(worstSolutionIndex), offspringPopulation.get(0)) > 0) {
            tree.removeSolution(population.get(worstSolutionIndex));
//            System.out.println("TREE AFTER REMOVING");
//            tree.printTree();
            tree.addSolution(offspringPopulation.get(0));
//            System.out.println("TREE AFTER ADDING");
//            tree.printTree();
            population.remove(worstSolutionIndex);
            population.add(offspringPopulation.get(0));
        }
        System.out.println(String.format("%d FITNESS: %.16f, %.16f",evaluations, fitness(), metric()));
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

        S solution = getBest();
        matingPopulation.add(solution);
        matingPopulation.add((S) tree.distanced(solution));
        return matingPopulation;
    }

    @Override
    protected List<S> evaluatePopulation(List<S> population) {
        if (tree.isEmpty()) {
            tree.createTree(population);
//            tree.printTree();

        }

        OurSolutionComparator comparator = new OurSolutionComparator();
        population.sort(comparator);

        for (S solution : population) {
            getProblem().evaluate(solution);
        }
        return population;
    }

    @Override
    public S getResult() {
        Collections.sort(getPopulation(), comparator);
        //TODO
        //save results to file!
        return getPopulation().get(0);
    }

    @Override
    public void initProgress() {
        evaluations = 1;
    }

    @Override
    public void updateProgress() {
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

    private S getBest(){
        Random generator = new Random();
        int i = generator.nextInt(getMaxPopulationSize()/5);
        Collections.sort(getPopulation(), comparator);
        return getPopulation().get(i);
    }
}
