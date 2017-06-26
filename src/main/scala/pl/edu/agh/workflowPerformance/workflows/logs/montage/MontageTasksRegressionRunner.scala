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
  val gradientDescent = Regressions.gradientDescent(runsQuantity = 2)
  val decisionTree = Regressions.decisionTree(maxDepth = 1000, runsQuantity = 1)
  val randomForest = Regressions.randomForest(estimatorsNumber = 50, maxDepth = 1000, runsQuantity = 1)
  val extraTrees = Regressions.extraTrees(estimatorsNumber = 50, maxDepth = 1000, runsQuantity = 1)
  val adaBoosting = Regressions.adaBoosting(estimatorsNumber = 100, maxDepth = 1000, runsQuantity = 1)
  val stochasticGradientBoosting = Regressions.stochasticGradientBoosting(estimatorsNumber = 100, runsQuantity = 1)
  val nearestNeighbours5 = Regressions.nearestNeighbours(neighboursNumber = 5, runsQuantity = 2)
  val nearestNeighbours10 = Regressions.nearestNeighbours(neighboursNumber = 10, runsQuantity = 2)

  val neuralNetworks = List(
    Regressions.multilayerPerceptron(layers = 2, layerSize = 30, runsQuantity = 1),
    Regressions.multilayerPerceptron(layers = 5, layerSize = 30, runsQuantity = 1)
  )

  val cList = List[Double](1.0, 100.0)
  val epsilonList = List[Double](0.0001)
  val svm = cList flatMap { c =>
    epsilonList map { epsilon =>
      Regressions.svm(SvmKernels.Rbf, c = c, epsilon = epsilon, runsQuantity = 1)
    }
  }

  val linearRegressions: List[Regression] = Nil//List(normalEquations, gradientDescent)
  val allRegressions: List[Regression] = List(normalEquations, gradientDescent, decisionTree, randomForest, extraTrees,
    adaBoosting, stochasticGradientBoosting, nearestNeighbours5, nearestNeighbours10) ++ neuralNetworks ++ svm

  override val convertersRegression: Map[AbstractFeatureConverter[MontageRow], List[Regression]] = Map(
    ConverterLinearFull -> allRegressions,
    ConverterLinearNoCores -> linearRegressions,
    ConverterLinearNoCoresAndMem -> linearRegressions,
    ConverterLinearNoOutputSize -> linearRegressions,
    ConverterLinearNoDataSizes -> linearRegressions,
    ConverterLinearNoInstance -> linearRegressions,
    ConverterLinearMontageSquare -> linearRegressions,
    ConverterLinearNoNetwork -> linearRegressions,
    ConverterCoresInversion -> linearRegressions,
    ConverterCoresAndMemInversion -> linearRegressions
  )

  override val headerInInputFiles: Boolean = false
  override val inputDataDir: String = resourcesData("montageWorkflows") + "/tasksLogs"
  val longTasks = Set("mBackground", "mDiffFit", "mProjectPP")
  override val tasks: List[String] = listFilesFrom(inputDataDir).filterNot(longTasks.contains)
//  override val tasks: List[String] = List("mBackground")
  override val outputDir: String = "results/montageTasks"
  override val includeConverterNamesInErrorComparisonPlots: Boolean = true
  override val topNErrorComparisonPlot: Option[Int] = Some(15)

  def main(args: Array[String]): Unit = {
    runAllTasks()
  }

}
