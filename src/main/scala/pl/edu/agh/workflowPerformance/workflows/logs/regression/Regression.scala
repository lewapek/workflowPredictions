package pl.edu.agh.workflowPerformance.workflows.logs.regression

import pl.edu.agh.workflowPerformance.workflows.Error

/**
  * @author lewap
  * @since 26.02.17
  */
abstract class Regression {

  val name: String
  val runs: Int

  def function(inputFilename: String): Error

}
