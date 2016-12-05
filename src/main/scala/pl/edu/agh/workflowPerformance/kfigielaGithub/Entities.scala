package pl.edu.agh.workflowPerformance.kfigielaGithub

import pl.edu.agh.workflowPerformance.utils.DummyCoding

/**
  * @author lewap
  * @since 14.11.16
  */
object Entities {

  case class Instance(name: String, price: Double, cores: Int, memoryGiB: Double) {

    def asFeatureVector: List[AnyVal] =
      price :: cores :: memoryGiB :: Nil

  }


  case class Task(name: String, sequenceNumber: Int, taskCount: Int, dataSizeInput: Double,
                  dataSizeOutput: Double) extends DummyCoding {

    def asFeatureVector: List[AnyVal] =
      taskCount :: dataSizeInput :: dataSizeOutput :: Nil

  }


  case class WorkflowTask(name: String, sequenceNumber: Int, taskCount: Int, singleExecTime: Double,
                          dataSizeInput: Double, dataSizeOutput: Double) extends DummyCoding {

    def asFeatureVector: List[AnyVal] =
      taskCount :: singleExecTime :: dataSizeInput :: dataSizeOutput :: Nil

  }


  case class ExecTime(task: Task, workflowTask: Option[WorkflowTask], instance: Instance, time: Double)

}
