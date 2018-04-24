import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.singleobjective.Rastrigin;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.ArrayList;
import java.util.List;


public class OurSteadyStateGeneticAlgorithmRunner {

    public static void main(String[] args) throws Exception {
        int[] variables = {20, 50, 100};
        Algorithm<DoubleSolution> algorithm;
        int populationSize = 30;
        int maxEvaluations = 5000;
        int SAMPLES = 10;

        for (int v : variables) {
            DoubleProblem problem = new Rastrigin(v);

            CrossoverOperator<DoubleSolution> crossoverOperator =
                    new SBXCrossover(0.9, 20.0);
            MutationOperator<DoubleSolution> mutationOperator =
                    new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0);
            OurCSVWriter writer = new OurCSVWriter(".csv");

//        algorithm = new RandomSteadyStateGeneticAlgorithm<>(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, writer);
            writer.setPath(String.format("kdtree_leaf_rebuild_%d.csv", v));
            writer.clear();
            for (int i = 0; i < SAMPLES; i++) {
                algorithm = new OurSteadyStateGeneticAlgorithm<>(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, writer, false, 50);
                run(algorithm);
            }


            writer.setPath(String.format("kdtree_leaf_best_balanced_%d.csv", v));
            writer.clear();
            for (int i = 0; i < SAMPLES; i++) {
                algorithm = new OurSteadyStateGeneticAlgorithm<>(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, writer, true, 50);
                run(algorithm);
            }

            writer.setPath(String.format("random_%d.csv", v));
            writer.clear();
            for (int i = 0; i < SAMPLES; i++) {
                algorithm = new RandomSteadyStateGeneticAlgorithm<>(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, writer);
                run(algorithm);
            }
        }
    }

    private static void run(Algorithm<DoubleSolution> algorithm){
        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();
        long computingTime = algorithmRunner.getComputingTime();
        DoubleSolution solution = algorithm.getResult();
        List<DoubleSolution> population = new ArrayList<>(1);
        population.add(solution);

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        JMetalLogger.logger.info("Fitness: " + solution.getObjective(0));

    }
}
