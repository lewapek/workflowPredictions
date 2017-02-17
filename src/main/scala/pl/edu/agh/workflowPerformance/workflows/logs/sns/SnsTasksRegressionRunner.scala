package pl.edu.agh.workflowPerformance.workflows.logs.sns

import pl.edu.agh.workflowPerformance.workflows.logs.sns.featureConverters.ConverterLinearFull
import pl.edu.agh.workflowPerformance.workflows.logs.sns.structure.{SnsRow, SnsRowParser}
import pl.edu.agh.workflowPerformance.workflows.logs.{AbstractFeatureConverter, RegressionRunnerUtils}

/**
  * @author lewap
  * @since 17.02.17
  */
object SnsTasksRegressionRunner extends RegressionRunnerUtils[SnsRow] with SnsRowParser {

  override def parseRowString(row: String): SnsRow =
    parseSnsRow(row)

  override val converters: List[AbstractFeatureConverter[SnsRow]] = List(ConverterLinearFull)
  override val inputDataDir: String = resourcesData("snsWorkflows")
  override val tasks: List[String] = listFilesFrom(inputDataDir)
  override val headerInInputFiles: Boolean = false
  override val outputDir: String = "results/snsTasks"

  def main(args: Array[String]): Unit = {
    runAllTasks()
  }

}
