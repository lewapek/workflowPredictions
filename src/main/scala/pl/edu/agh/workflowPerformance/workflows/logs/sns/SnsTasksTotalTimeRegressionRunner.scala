package pl.edu.agh.workflowPerformance.workflows.logs.sns

import pl.edu.agh.workflowPerformance.workflows.logs.regression.{AbstractFeatureConverter, Regression, RegressionRunnerUtils, Regressions}
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

  override def parseRowString(row: String): SnsTotalTimeRow =
    parseSnsTotalTimeRow(row)

  override val convertersRegression: Map[AbstractFeatureConverter[SnsTotalTimeRow], List[Regression]] = Map(
    ConverterLinearFull -> List(normalEquations, gradientDescent, decisionTree)
  )

  override val inputDataDir: String = resourcesData("snsWorkflows/totalTimes")
  override val tasks: List[String] = listFilesFrom(inputDataDir)
  override val headerInInputFiles: Boolean = true
  override val outputDir: String = "results/snsTasks/totalTime"

  def main(args: Array[String]): Unit = {
    runAllTasks()
  }

}
