import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.selection.RandomSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.*;


public class OurSteadyStateGeneticAlgorithm<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, S> {
    private Comparator<S> comparator;
    private int maxEvaluations;
    private int evaluations;
    private IKDTree tree;
    private OurCSVWriter writer;

    private int rebuild;
    private boolean bestSelect;

    private int algorithmType;

    /**
     * Constructor
     */
    OurSteadyStateGeneticAlgorithm(Problem<S> problem, int maxEvaluations, int populationSize,
                                          CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                                          OurCSVWriter writer, boolean bestSelect, int rebuild, int algorithmType) {
        super(problem);
        setMaxPopulationSize(populationSize);
        this.maxEvaluations = maxEvaluations;

        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;

        this.selectionOperator = new RandomSelection<>();
        this.tree = new KDTree<>();

        comparator = new ObjectiveComparator<S>(0);

        this.writer = writer;

        this.bestSelect = bestSelect;
        this.rebuild = rebuild;
        this.algorithmType = algorithmType;
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
            tree.removeSolution(population.get(worstSolutionIndex));
            tree.addSolution(offspringPopulation.get(0));
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

        S solution;
        if (bestSelect) {
            solution = getBest(population);
        } else {
            solution = selectionOperator.execute(population);
        }
        matingPopulation.add(solution);
        matingPopulation.add((S) tree.distanced(solution, algorithmType));
        return matingPopulation;
    }

    @Override
    protected List<S> evaluatePopulation(List<S> population) {
        if (tree.isEmpty()) {
            tree.createTree(population);
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
        return getPopulation().get(0);
    }

    @Override
    public void initProgress() {
        evaluations = 1;
    }

    @Override
    public void updateProgress() {
        writer.write(evaluations, fitness(), 0);
//        if (evaluations % 50 == 0) {
//            System.out.println(evaluations + " " + fitness() + " " + metric());
//        }
        if (evaluations % rebuild == 0) {
            tree.rebuildTree(getPopulation());
        }
        evaluations++;
    }

    @Override
    public String getName() {
        return "ssGA_KDTree" + algorithmType + "_" + getProblem().getName();
    }

    @Override
    public String getDescription() {
        return "KD-Tree Steady-State Genetic Algorithm";
    }

    private double metric() {
        int size = getProblem().getNumberOfVariables();
        int popSize = getPopulation().size();
        double[] tmp = new double[size];
        double[] std = new double[size];
        Arrays.fill(tmp, 0);
        Arrays.fill(std, 0);
        //dodajemy wszystkie wartości
        for (S s : getPopulation()) {
            for (int i = 0; i < size; i++) {
                tmp[i] += Double.parseDouble(s.getVariableValueString(i));
            }
        }
        //liczymy średnią
        for (int i = 0; i < size; i++) {
            tmp[i] /= popSize;
        }
        //obliczamy sume do std
        for (S s : getPopulation()) {
            for (int i = 0; i < size; i++) {
                std[i] += Math.pow(Double.parseDouble(s.getVariableValueString(i)) - tmp[i], 2);
            }
        }
        Arrays.sort(std);
        return Math.sqrt(std[0]);
    }

    private double fitness() {
        Collections.sort(getPopulation(), comparator);
        return getPopulation().get(0).getObjective(0);
    }

    private S getBest(List<S> population) {
        Random generator = new Random();
        int i = generator.nextInt(getMaxPopulationSize() / 5);
        Collections.sort(population, comparator);
        return population.get(i);
    }
}
