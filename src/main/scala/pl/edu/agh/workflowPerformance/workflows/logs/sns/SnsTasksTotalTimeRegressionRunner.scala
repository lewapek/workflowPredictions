package pl.edu.agh.workflowPerformance.workflows.logs.sns

import pl.edu.agh.workflowPerformance.workflows.logs.regression.{Regressions, _}
import pl.edu.agh.workflowPerformance.workflows.logs.sns.featureConverters.totalTime.ConverterLinearFull
import pl.edu.agh.workflowPerformance.workflows.logs.sns.structure.{SnsRowParser, SnsTotalTimeRow}

/**
  * @author lewap
  * @since 17.02.17
  */
object SnsTasksTotalTimeRegressionRunner extends RegressionRunnerUtils[SnsTotalTimeRow] with SnsRowParser {

  val normalEquations = Regressions.normalEquations(runsQuantity = 1)
  val gradientDescent = Regressions.gradientDescent(runsQuantity = 5)
  val decisionTree = Regressions.decisionTree(maxDepth = 10, runsQuantity = 5)

  val nearestNeighbours5 = Regressions.nearestNeighbours(neighboursNumber = 5, runsQuantity = 5)
  val nearestNeighbours10 = Regressions.nearestNeighbours(neighboursNumber = 10, runsQuantity = 5)

  val neuralNetworks = List(
    Regressions.multilayerPerceptron(layers = 0, layerSize = 0, runsQuantity = 5),
    Regressions.multilayerPerceptron(layers = 1, layerSize = 20, runsQuantity = 5),
    Regressions.multilayerPerceptron(layers = 5, layerSize = 20, runsQuantity = 5),
    Regressions.multilayerPerceptron(layers = 1, layerSize = 100, runsQuantity = 5),
    Regressions.multilayerPerceptron(layers = 5, layerSize = 100, runsQuantity = 5)
  )

  val cList = List(1.0, 10.0, 100.0, 1000.0)
  val epsilonList = List(0.1, 0.01)
  val smv = cList flatMap { c =>
    epsilonList map { epsilon =>
      Regressions.svm(SvmKernels.Rbf, c = c, epsilon = epsilon, runsQuantity = 5)
    }
  }


  override def parseRowString(row: String): SnsTotalTimeRow =
    parseSnsTotalTimeRow(row)

  override val convertersRegression: Map[AbstractFeatureConverter[SnsTotalTimeRow], List[Regression]] = Map(
    ConverterLinearFull -> List(normalEquations, gradientDescent, decisionTree, nearestNeighbours5, nearestNeighbours10).++(smv)
  )

  override val inputDataDir: String = resourcesData("snsWorkflows/totalTimes")
  override val tasks: List[String] = listFilesFrom(inputDataDir)
  override val headerInInputFiles: Boolean = true
  override val outputDir: String = "results/snsTasks/totalTime"

  def main(args: Array[String]): Unit = {
    runAllTasks()
  }

}
