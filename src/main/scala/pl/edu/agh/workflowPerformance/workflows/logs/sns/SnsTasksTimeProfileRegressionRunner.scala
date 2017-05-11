package pl.edu.agh.workflowPerformance.workflows.logs.sns

import pl.edu.agh.workflowPerformance.workflows.logs.regression._
import pl.edu.agh.workflowPerformance.workflows.logs.sns.featureConverters.timeProfile.ConverterLinearFull
import pl.edu.agh.workflowPerformance.workflows.logs.sns.structure.{SnsProfileRow, SnsRowParser}

/**
  * @author lewap
  * @since 17.02.17
  */
object SnsTasksTimeProfileRegressionRunner extends RegressionRunnerUtils[SnsProfileRow] with SnsRowParser {

  val normalEquations = Regressions.normalEquations(runsQuantity = 1)
  val gradientDescent = Regressions.gradientDescent(runsQuantity = 3)
  val decisionTree = Regressions.decisionTree(maxDepth = 10, runsQuantity = 3)
  val randomForest = Regressions.randomForest(maxDepth = 10, runsQuantity = 1)
  val extraTrees = Regressions.extraTrees(maxDepth = 10, runsQuantity = 1)
  val adaBoosting = Regressions.adaBoosting(maxDepth = 10, runsQuantity = 1)
  val stochasticGradientBoosting = Regressions.stochasticGradientBoosting(runsQuantity = 1)
  val nearestNeighbours5 = Regressions.nearestNeighbours(neighboursNumber = 5, runsQuantity = 3)
  val nearestNeighbours10 = Regressions.nearestNeighbours(neighboursNumber = 10, runsQuantity = 3)

  val neuralNetworks = List(
    Regressions.multilayerPerceptron(layers = 1, layerSize = 20, runsQuantity = 2),
    Regressions.multilayerPerceptron(layers = 5, layerSize = 5, runsQuantity = 2),
    Regressions.multilayerPerceptron(layers = 5, layerSize = 20, runsQuantity = 2),
    Regressions.multilayerPerceptron(layers = 20, layerSize = 20, runsQuantity = 2)
  )

  val cList = List[Double](1.0, 100.0)
  val epsilonList = List[Double](0.0001)
  val svm = cList flatMap { c =>
    epsilonList map { epsilon =>
      Regressions.svm(SvmKernels.Rbf, c = c, epsilon = epsilon, runsQuantity = 1)
    }
  }

  override val indexingComparisonPlotMode: Boolean = true

  override def parseRowString(row: String): SnsProfileRow =
    parseSnsProfileRow(row)

  override val convertersRegression: Map[AbstractFeatureConverter[SnsProfileRow], List[Regression]] = Map(
    ConverterLinearFull -> (List(normalEquations, gradientDescent, decisionTree, randomForest, /*extraTrees, */adaBoosting,
      stochasticGradientBoosting, nearestNeighbours5, nearestNeighbours10) ++ neuralNetworks ++ svm)
  )

  override val inputDataDir: String = resourcesData("snsWorkflows")
  override val tasks: List[String] = listFilesFrom(inputDataDir)
  override val headerInInputFiles: Boolean = true
  override val outputDir: String = "results/snsTasks/timeProfile"

  def main(args: Array[String]): Unit = {
    runAllTasks()
  }

}
