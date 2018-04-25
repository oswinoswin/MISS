import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.singleobjective.Rastrigin;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;


public class OurSteadyStateGeneticAlgorithmRunner {

    public static void main(String[] args) throws Exception {
        int[] variables = {20};
        Algorithm<DoubleSolution> algorithm;
        int populationSize = 30;
        int maxEvaluations = 5000;
        int SAMPLES = 10;
        int type = 1;

        for (int v : variables) {
            DoubleProblem problem = new Rastrigin(v);

            CrossoverOperator<DoubleSolution> crossoverOperator =
                    new SBXCrossover(0.9, 20.0);
            MutationOperator<DoubleSolution> mutationOperator =
                    new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);
            OurCSVWriter writer = new OurCSVWriter(".csv");

//        algorithm = new RandomSteadyStateGeneticAlgorithm<>(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, writer);
            writer.setPath(String.format("kdtree_alg%d_rebuild_%d.csv", type, v));
            writer.clear();
            for (int i = 0; i < SAMPLES; i++) {
                algorithm = new OurSteadyStateGeneticAlgorithm<>(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, writer, false, 50, type);
                JMetalLogger.logger.info("Starting: KDTree  " + v + " size, " + i);
                run(algorithm);
            }


            writer.setPath(String.format("kdtree_alg%d_best_rebuild_%d.csv", type, v));
            writer.clear();
            for (int i = 0; i < SAMPLES; i++) {
                algorithm = new OurSteadyStateGeneticAlgorithm<>(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, writer, true, 50, type);
                JMetalLogger.logger.info("Starting: Best  " + v + " size, " + i);
                run(algorithm);
            }

            writer.setPath(String.format("random_best_%d.csv", v));
            writer.clear();
            for (int i = 0; i < SAMPLES; i++) {
                algorithm = new RandomSteadyStateGeneticAlgorithm<>(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, writer);
                JMetalLogger.logger.info("Starting: Random  " + v + " size, " + i);
                run(algorithm);
            }

        }
    }

    private static void run(Algorithm<DoubleSolution> algorithm) {
        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();
        long computingTime = algorithmRunner.getComputingTime();
        DoubleSolution solution = algorithm.getResult();
        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Fitness: " + solution.getObjective(0));

    }
}
