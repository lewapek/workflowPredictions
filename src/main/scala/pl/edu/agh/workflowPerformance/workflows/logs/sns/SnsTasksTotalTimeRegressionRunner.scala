package pl.edu.agh.workflowPerformance.workflows.logs.sns

import pl.edu.agh.workflowPerformance.workflows.logs.regression._
import pl.edu.agh.workflowPerformance.workflows.logs.sns.featureConverters.totalTime.ConverterLinearFull
import pl.edu.agh.workflowPerformance.workflows.logs.sns.structure.{SnsRowParser, SnsTotalTimeRow}

/**
  * @author lewap
  * @since 17.02.17
  */
object SnsTasksTotalTimeRegressionRunner extends RegressionRunnerUtils[SnsTotalTimeRow] with SnsRowParser {

  val normalEquations = Regressions.normalEquations(runsQuantity = 1)
  val gradientDescent = Regressions.gradientDescent(runsQuantity = 3)
  val decisionTree = Regressions.decisionTree(maxDepth = 10, runsQuantity = 1)
  val randomForest = Regressions.randomForest(maxDepth = 10, runsQuantity = 1)
  val extraTrees = Regressions.extraTrees(maxDepth = 10, runsQuantity = 1)
  val adaBoosting = Regressions.adaBoosting(maxDepth = 10, runsQuantity = 1)
  val stochasticGradientBoosting = Regressions.stochasticGradientBoosting(runsQuantity = 1)
  val nearestNeighbours5 = Regressions.nearestNeighbours(neighboursNumber = 5, runsQuantity = 3)
  val nearestNeighbours10 = Regressions.nearestNeighbours(neighboursNumber = 10, runsQuantity = 3)

  val neuralNetworks = List(
    Regressions.multilayerPerceptron(layers = 2, layerSize = 30, maxIterations = 2000, runsQuantity = 3),
    Regressions.multilayerPerceptron(layers = 5, layerSize = 30, maxIterations = 2000, runsQuantity = 3),
    Regressions.multilayerPerceptron(layers = 20, layerSize = 30, maxIterations = 2000, runsQuantity = 3)
  )

  val cList = List[Double](1.0, 100.0)
  val epsilonList = List[Double](0.0001)
  val svm = cList flatMap { c =>
    epsilonList map { epsilon =>
      Regressions.svm(SvmKernels.Rbf, c = c, epsilon = epsilon, runsQuantity = 1)
    }
  }

  override val splitFactorOption = Some(0.8)

  override def parseRowString(row: String): SnsTotalTimeRow =
    parseSnsTotalTimeRow(row)

  override val convertersRegression: Map[AbstractFeatureConverter[SnsTotalTimeRow], List[Regression]] = Map(
    ConverterLinearFull -> (List(normalEquations, gradientDescent, decisionTree, randomForest, extraTrees, adaBoosting,
      stochasticGradientBoosting, nearestNeighbours5, nearestNeighbours10) ++ neuralNetworks ++ svm)
  )

  override val inputDataDir: String = resourcesData("snsWorkflows/totalTimes")
  override val tasks: List[String] = listFilesFrom(inputDataDir)
  override val headerInInputFiles: Boolean = true
  override val outputDir: String = "results/snsTasks/totalTime"

  def main(args: Array[String]): Unit = {
    runAllTasks()
  }

}
