package pl.edu.agh.workflowPerformance.workflows.logs.montage

import pl.edu.agh.workflowPerformance.workflows.logs.montage.featureConverters._
import pl.edu.agh.workflowPerformance.workflows.logs.montage.structure.{MontageRow, MontageRowParser}
import pl.edu.agh.workflowPerformance.workflows.logs.regression._

/**
  * @author lewap
  * @since 16.02.17
  */
object MontageTasksRegressionRunner extends RegressionRunnerUtils[MontageRow] with MontageRowParser {

  override def parseRowString(row: String): MontageRow =
    parseMontageRow(row)

  val normalEquations = Regressions.normalEquations(runsQuantity = 1)
  val gradientDescent = Regressions.gradientDescent(runsQuantity = 3)
  val decisionTree = Regressions.decisionTree(maxDepth = 10, runsQuantity = 3)
  val randomForest = Regressions.randomForest(maxDepth = 10, runsQuantity = 3)
  val extraTrees = Regressions.extraTrees(maxDepth = 10, runsQuantity = 3)
  val adaBoosting = Regressions.adaBoosting(maxDepth = 10, runsQuantity = 3)
  val stochasticGradientBoosting = Regressions.stochasticGradientBoosting(runsQuantity = 3)
  val nearestNeighbours5 = Regressions.nearestNeighbours(neighboursNumber = 5, runsQuantity = 3)
  val nearestNeighbours10 = Regressions.nearestNeighbours(neighboursNumber = 10, runsQuantity = 3)

  val neuralNetworks = List(
    Regressions.multilayerPerceptron(layers = 0, layerSize = 0, runsQuantity = 3),
    Regressions.multilayerPerceptron(layers = 1, layerSize = 20, runsQuantity = 3),
    Regressions.multilayerPerceptron(layers = 5, layerSize = 20, runsQuantity = 3),
    Regressions.multilayerPerceptron(layers = 1, layerSize = 100, runsQuantity = 3),
    Regressions.multilayerPerceptron(layers = 5, layerSize = 100, runsQuantity = 3)
  )

  val cList = List[Double](1.0, 100.0)
  val epsilonList = List[Double](0.0001)
  val svm = cList flatMap { c =>
    epsilonList map { epsilon =>
      Regressions.svm(SvmKernels.Rbf, c = c, epsilon = epsilon, runsQuantity = 3)
    }
  }

  val linearRegressions: List[Regression] = List(normalEquations, gradientDescent)
  val allRegressions: List[Regression] = List(normalEquations, gradientDescent, decisionTree, randomForest, extraTrees,
    adaBoosting, stochasticGradientBoosting, nearestNeighbours5, nearestNeighbours10) ++ neuralNetworks ++ svm

  override val convertersRegression: Map[AbstractFeatureConverter[MontageRow], List[Regression]] = Map(
    ConverterLinearFull -> allRegressions,
    ConverterLinearNoCores -> linearRegressions,
    ConverterLinearNoCoresAndMem -> linearRegressions,
    ConverterLinearNoOutputSize -> linearRegressions,
    ConverterLinearNoDataSizes -> allRegressions,
    ConverterLinearNoInstance -> linearRegressions,
    ConverterLinearNoInstanceMontageSquare -> linearRegressions,
    ConverterLinearNoNetwork -> linearRegressions,
    ConverterCoresInversion -> linearRegressions,
    ConverterCoresAndMemInversion -> linearRegressions
  )

  override val headerInInputFiles: Boolean = false
  override val inputDataDir: String = resourcesData("montageWorkflows") + "/tasksLogs"
  override val tasks: List[String] = listFilesFrom(inputDataDir)
  override val outputDir: String = "results/montageTasks"

  def main(args: Array[String]): Unit = {
    runAllTasks()
  }

}
