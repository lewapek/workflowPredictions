package pl.edu.agh.workflowPerformance.workflows

import pl.edu.agh.workflowPerformance.utils.DummyCoding

/**
  * @author lewap
  * @since 14.11.16
  */
object Entities {

  case class Instance(name: String, price: Double, cores: Int, memoryGiB: Double, network: Double) {

    def asFeatureVector: List[AnyVal] =
      price :: cores :: memoryGiB :: network :: Nil

  }

  object Instance {
    def convertYamlMontageNameToLogsName(yamlMontageName: String): String =
      yamlMontageName.replace('-', '.')

    def convertLogsNameToYamlMontageName(logsName: String): String =
      logsName.replace('.', '-')
  }


  case class Task(name: String, sequenceNumber: Int, taskCount: Int, dataSizeInput: Double,
                  dataSizeOutput: Double, taskExtension: Option[TaskExtension] = None) extends DummyCoding {

    def asFeatureVector: List[AnyVal] =
      taskCount :: dataSizeInput :: dataSizeOutput :: Nil

  }

  case class TaskExtension(parameters: List[AnyVal])


  case class WorkflowTask(name: String, sequenceNumber: Int, taskCount: Int, singleExecTime: Double,
                          dataSizeInput: Double, dataSizeOutput: Double) extends DummyCoding {

    def asFeatureVector: List[AnyVal] =
      taskCount :: singleExecTime :: dataSizeInput :: dataSizeOutput :: Nil

  }


  case class Performance(task: Task, workflowTask: Option[WorkflowTask], instance: Instance,
                         normalizedTimeToCores: Double, performance: Double)

}
