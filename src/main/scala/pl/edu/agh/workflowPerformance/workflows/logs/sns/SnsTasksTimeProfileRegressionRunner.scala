package pl.edu.agh.workflowPerformance.workflows.logs.sns

import pl.edu.agh.workflowPerformance.workflows.logs.regression.{AbstractFeatureConverter, Regression, RegressionRunnerUtils, Regressions}
import pl.edu.agh.workflowPerformance.workflows.logs.sns.featureConverters.timeProfile.ConverterLinearFull
import pl.edu.agh.workflowPerformance.workflows.logs.sns.structure.{SnsProfileRow, SnsRowParser}

/**
  * @author lewap
  * @since 17.02.17
  */
object SnsTasksTimeProfileRegressionRunner extends RegressionRunnerUtils[SnsProfileRow] with SnsRowParser {

  val normalEquations = Regressions.normalEquations(runsQuantity = 1)
  val gradientDescent = Regressions.gradientDescent(runsQuantity = 5)
  val decisionTree = Regressions.decisionTree(maxDepth = 10, runsQuantity = 5)

  override def parseRowString(row: String): SnsProfileRow =
    parseSnsProfileRow(row)

  override val convertersRegression: Map[AbstractFeatureConverter[SnsProfileRow], List[Regression]] = Map(
    ConverterLinearFull -> List(normalEquations , gradientDescent, decisionTree)
  )

  override val inputDataDir: String = resourcesData("snsWorkflows")
  override val tasks: List[String] = listFilesFrom(inputDataDir)
  override val headerInInputFiles: Boolean = true
  override val outputDir: String = "results/snsTasks/timeProfile"

  def main(args: Array[String]): Unit = {
    runAllTasks()
  }

}
