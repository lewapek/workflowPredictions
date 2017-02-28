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
  val gradientDescent = Regressions.gradientDescent(runsQuantity = 5)
  val decisionTree = Regressions.decisionTree(maxDepth = 10, runsQuantity = 5)

  override val convertersRegression: Map[AbstractFeatureConverter[MontageRow], List[Regression]] = Map(
    ConverterLinearFull -> List(normalEquations, gradientDescent, decisionTree),
    ConverterLinearNoCores -> List(normalEquations, gradientDescent, decisionTree),
    ConverterLinearNoCoresAndMem -> List(normalEquations, gradientDescent, decisionTree),
    ConverterLinearNoOutputSize -> List(normalEquations, gradientDescent, decisionTree),
    ConverterLinearNoDataSizes -> List(normalEquations, gradientDescent, decisionTree),
    ConverterLinearNoInstance -> List(normalEquations, gradientDescent, decisionTree),
    ConverterLinearNoInstanceMontageSquare -> List(normalEquations, gradientDescent, decisionTree),
    ConverterLinearNoNetwork -> List(normalEquations, gradientDescent, decisionTree),
    ConverterCoresInversion -> List(normalEquations, gradientDescent, decisionTree),
    ConverterCoresAndMemInversion -> List(normalEquations, gradientDescent, decisionTree)
  )

  override val headerInInputFiles: Boolean = false
  override val inputDataDir: String = resourcesData("montageWorkflows") + "/tasksLogs"
  override val tasks: List[String] = listFilesFrom(inputDataDir)
  override val outputDir: String = "results/montageTasks"

  def main(args: Array[String]): Unit = {
    runAllTasks()
  }

}
