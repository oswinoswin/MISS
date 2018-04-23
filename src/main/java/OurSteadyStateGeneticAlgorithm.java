import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.selection.RandomSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.*;
import java.util.logging.Logger;


import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OurSteadyStateGeneticAlgorithm<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, S> {
    private Comparator<S> comparator;
    private int maxEvaluations;
    private int evaluations;
    private IKDTree tree;
    List<String> lines;


    private final static Logger LOGGER = Logger.getLogger(OurSteadyStateGeneticAlgorithm.class.getName());

    public OurSteadyStateGeneticAlgorithm(Problem<S> problem, int maxEvaluations, int populationSize,
                                          CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator) {
        super(problem);
        setMaxPopulationSize(populationSize);
        this.maxEvaluations = maxEvaluations;

        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;

        this.selectionOperator = new RandomSelection<>();
        this.tree = new KDTree<>();

        comparator = new ObjectiveComparator<S>(0);
        this.lines = new ArrayList<>();

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

        S solution = getBest();
        matingPopulation.add(solution);
        matingPopulation.add((S) tree.distanced(solution));
        return matingPopulation;
    }

    @Override
    protected List<S> evaluatePopulation(List<S> population) {
        System.out.println("FITNESS: "+ this.getResult().getObjective(0) + " ITERATIONS: " + this.evaluations );
        lines.add(this.evaluations + ", " + this.getResult().getObjective(0));
        if (tree.isEmpty()) {
            tree.createTree(population);
            System.out.println("Evaluations: " + evaluations);
            System.out.println("Distanced: " + tree.distanced(tree.getRoot()));
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
        if(evaluations == maxEvaluations){
            System.out.println("Finished!");
            System.out.println(lines);
            try{
                Path file = Paths.get("the-file-name.txt");
                Files.write(file, lines, Charset.forName("UTF-8"));
            }catch (Exception e){
                System.out.println("Can't write to file");
            }
        }
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
        int i = generator.nextInt(getMaxPopulationSize()/5) + 1;
        Collections.sort(getPopulation(), comparator);
        return getPopulation().get(i);
    }
}
