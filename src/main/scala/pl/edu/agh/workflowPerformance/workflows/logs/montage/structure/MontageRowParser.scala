package pl.edu.agh.workflowPerformance.workflows.logs.montage.structure

import pl.edu.agh.workflowPerformance.workflows.Entities.Instance
import pl.edu.agh.workflowPerformance.workflows.logs.montage.DeprecatedRegressionRunner.Infrastructure

/**
  * @author lewap
  * @since 26.01.17
  */
trait MontageRowParser {
  def parseMontageRow(line: String): MontageRow = {
    val splitLine = line.trim.split(' ')
    val instanceName = Instance.convertLogsNameToYamlMontageName(splitLine.head)

    val instance = Infrastructure.instances(instanceName)
    val montage = splitLine(1).toDouble
    val time = splitLine(6).toDouble
    val performance = 1 / time
    val inputDataSize = splitLine(7).toLong
    val outputDataSize = splitLine(8).toLong

    MontageRow(time, performance, instance, montage, inputDataSize, outputDataSize)
  }
}
