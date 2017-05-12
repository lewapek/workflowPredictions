package pl.edu.agh.workflowPerformance.workflows.logs.regression

import pl.edu.agh.workflowPerformance.workflows.Errors

/**
  * @author lewap
  * @since 26.02.17
  */
abstract class Regression {

  val name: String
  val runs: Int

  def function(inputFilename: String, splitFactor: Double, split: Option[Int] = None): Errors

}
