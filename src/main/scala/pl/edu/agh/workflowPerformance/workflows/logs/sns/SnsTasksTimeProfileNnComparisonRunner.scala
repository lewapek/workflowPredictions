package pl.edu.agh.workflowPerformance.workflows.logs.sns

import pl.edu.agh.workflowPerformance.workflows.logs.regression._
import pl.edu.agh.workflowPerformance.workflows.logs.sns.featureConverters.timeProfile.ConverterLinearFull
import pl.edu.agh.workflowPerformance.workflows.logs.sns.structure.{SnsNnComparisonRowParser, SnsProfileRow}

/**
  * @author lewap
  * @since 17.02.17
  */
object SnsTasksTimeProfileNnComparisonRunner extends RegressionRunnerUtils[SnsProfileRow] with SnsNnComparisonRowParser {

  // mode to change: 0-3
  val yMode = 0
  val modeNames = Map(0 -> "stime", 1 -> "utime", 2 -> "read_bytes", 3 -> "write_bytes")
  val yModes = Map(
    0 -> parseSnsProfileRowWithStime _,
    1 -> parseSnsProfileRowWithUtime _,
    2 -> parseSnsProfileRowWithWriteBytes _,
    3 -> parseSnsProfileRowWithReadBytes _
  )

  val decisionTree = Regressions.decisionTree(maxDepth = 10, runsQuantity = 1)
  val randomForest = Regressions.randomForest(maxDepth = 10, runsQuantity = 1)
  val extraTrees = Regressions.extraTrees(maxDepth = 10, runsQuantity = 1)
  val adaBoosting = Regressions.adaBoosting(maxDepth = 10, runsQuantity = 1)
  val stochasticGradientBoosting = Regressions.stochasticGradientBoosting(runsQuantity = 1)
  val nearestNeighbours5 = Regressions.nearestNeighbours(neighboursNumber = 5, runsQuantity = 1)
  val nearestNeighbours10 = Regressions.nearestNeighbours(neighboursNumber = 10, runsQuantity = 1)

  val neuralNetworks = List(
    Regressions.multilayerPerceptron(layers = 2, layerSize = 30, maxIterations=2000, solver = Mlp.Lbfgs, runsQuantity = 3),
    Regressions.multilayerPerceptron(layers = 5, layerSize = 30, maxIterations=2000, solver = Mlp.Lbfgs, runsQuantity = 3),
    Regressions.multilayerPerceptron(layers = 20, layerSize = 30, maxIterations=2000, solver = Mlp.Lbfgs, runsQuantity = 3)
  )

  val cList = List[Double](1.0, 100.0)
  val epsilonList = List[Double](0.0001)
  val svm = cList flatMap { c =>
    epsilonList map { epsilon =>
      Regressions.svm(SvmKernels.Rbf, c = c, epsilon = epsilon, runsQuantity = 1)
    }
  }

  override val tasksSplitParameter: Map[String, Int] = Map(
    "namd2" -> 581,
    "namd3" -> 2181,
    "sassena5" -> 1067,
    "sassena6" -> 409
  )
  override val indexingComparisonPlotMode: Boolean = true

  override def parseRowString(row: String): SnsProfileRow =
    yModes(yMode)(row)

  override val convertersRegression: Map[AbstractFeatureConverter[SnsProfileRow], List[Regression]] = Map(
    ConverterLinearFull -> (List(decisionTree, randomForest, extraTrees, adaBoosting, stochasticGradientBoosting,
      nearestNeighbours5, nearestNeighbours10) ++ neuralNetworks ++ svm)
  )

  override val inputDataDir: String = resourcesData("snsWorkflows/nnResults/tasks")
  override val tasks: List[String] = listFilesFrom(inputDataDir)
  override val headerInInputFiles: Boolean = false
  override val outputDir: String = "results/snsTasks/timeProfileNnComparison_" + modeNames(yMode)

  def main(args: Array[String]): Unit = {
    runAllTasks()
  }

}
