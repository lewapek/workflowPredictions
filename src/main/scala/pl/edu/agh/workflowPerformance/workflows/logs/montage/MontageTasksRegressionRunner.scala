package pl.edu.agh.workflowPerformance.workflows.logs.montage

import pl.edu.agh.workflowPerformance.workflows.logs.montage.featureConverters._
import pl.edu.agh.workflowPerformance.workflows.logs.{AbstractFeatureConverter, RegressionRunnerUtils}
import pl.edu.agh.workflowPerformance.workflows.logs.montage.structure.{MontageRow, MontageRowParser}

/**
  * @author lewap
  * @since 16.02.17
  */
object MontageTasksRegressionRunner extends RegressionRunnerUtils[MontageRow] with MontageRowParser {

  override def parseRowString(row: String): MontageRow =
    parseMontageRow(row)

  override val converters: List[AbstractFeatureConverter[MontageRow]] = List(
    ConverterLinearFull,
    ConverterLinearNoCores,
    ConverterLinearNoCoresAndMem,
    ConverterLinearNoOutputSize,
    ConverterLinearNoDataSizes,
    ConverterLinearNoInstance,
    ConverterLinearNoInstanceMontageSquare,
    ConverterLinearNoNetwork,
    ConverterCoresInversion,
    ConverterCoresAndMemInversion
  )
  override val headerInInputFiles: Boolean = false
  override val inputDataDir: String = resourcesData("montageWorkflows") + "/tasksLogs"
  override val tasks: List[String] = listFilesFrom(inputDataDir)
  override val outputDir: String = "results/tasksLogsRmse"

  def main(args: Array[String]): Unit = {
    runAllTasks()
  }

}
