import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.singleobjective.Griewank;
import org.uma.jmetal.problem.singleobjective.Rastrigin;
import org.uma.jmetal.problem.singleobjective.Rosenbrock;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class OurSteadyStateGeneticAlgorithmRunner {
    private static int POPULATION_SIZE = 30;
    private static int MAX_EVALUATIONS = 5000;
    private static int N = 10;
    private static OurCSVWriter writer = new OurCSVWriter(".csv");
    private static OurCSVWriter timeWriter = new OurCSVWriter("times.csv");


    public static void main(String[] args) throws Exception {
        List<DoubleProblem> problems = new LinkedList<>();
        List<Integer> variables = new ArrayList<>(Arrays.asList(20, 50, 100, 200, 500, 1000));
        timeWriter.clearTime();

        for (String arg : args) {
            if (arg.startsWith("-s")) {
                POPULATION_SIZE = Integer.parseInt(arg.substring(3));
            } else if (arg.startsWith("-v")) {
                variables.clear();
                for (String s : arg.substring(3).split(",")) {
                    variables.add(Integer.parseInt(s));
                }
            } else if (arg.startsWith("-m")) {
                MAX_EVALUATIONS = Integer.parseInt(arg.substring(3));
            } else if (arg.startsWith("-N")) {
                N = Integer.parseInt(arg.substring(3));
            } else {
                System.out.println("Usage: \n -s [population size] \n -v [list of variables number] \n -m [max evaluations] \n -N [samples]");
            }
        }

        JMetalLogger.logger.info(String.format("Parameter used in experiment: \nPopulation size = %d \nMax evaluations = %d \nsamples = %d\nproblem sizes = %s ", POPULATION_SIZE, MAX_EVALUATIONS, N, variables));

        for (int v : variables) {
            problems.add(new Rastrigin(v));
            problems.add(new Rosenbrock(v));
            problems.add(new Griewank(v));
            problems.add(new Ackley(v));
            problems.add(new Schwefel(v));
            problems.add(new DeJong(v));
        }

        for (DoubleProblem problem : problems) {
            run(problem);
        }
    }

    private static void run(DoubleProblem problem) {
        int size = problem.getNumberOfVariables();
        CrossoverOperator<DoubleSolution> crossoverOperator = new SBXCrossover(0.9, 20.0);
        MutationOperator<DoubleSolution> mutationOperator = new PolynomialMutation(1.0 / size, 20.0);
        int type = 1;

        writer.setPath(String.format("%s_ssga_alg%d_%d_%d.csv", problem.getName(), type, size, POPULATION_SIZE));
        writer.clear();
        Algorithm<DoubleSolution> algorithm;
        for (int i = 0; i < N; i++) {
            algorithm = new OurSteadyStateGeneticAlgorithm<>(problem, MAX_EVALUATIONS, POPULATION_SIZE, crossoverOperator, mutationOperator, writer, false, 50, type, 0);
            JMetalLogger.logger.info("Starting: KDTree " + problem.getName() + size);
            execute(algorithm);
        }

        writer.setPath(String.format("%s_ssga_random_%d_%d.csv", problem.getName(), size, POPULATION_SIZE));
        writer.clear();
        for (int i = 0; i < N; i++) {
            algorithm = new RandomSteadyStateGeneticAlgorithm<>(problem, MAX_EVALUATIONS, POPULATION_SIZE, crossoverOperator, mutationOperator, writer);
            JMetalLogger.logger.info("Starting: Random " + problem.getName() + size);
            execute(algorithm);

        }
    }

    private static void execute(Algorithm<DoubleSolution> algorithm) {
        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();
        long computingTime = algorithmRunner.getComputingTime();
        DoubleSolution solution = algorithm.getResult();
        timeWriter.writeTime(algorithm.getName(), solution.getNumberOfVariables(), computingTime);
        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Fitness: " + solution.getObjective(0));
    }
}
