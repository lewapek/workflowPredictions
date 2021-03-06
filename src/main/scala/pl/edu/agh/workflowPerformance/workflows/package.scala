package pl.edu.agh.workflowPerformance

/**
  * @author lewap
  * @since 25.02.17
  */
package object workflows {

  case class Errors(rmse: Double, mae: Double, absoluteDivMean: Double, relative: Double, runs: Int = 1)

  case class RegressionError(regressionName: String, error: Errors)

  case class ConverterError(converterName: String, converterShortName: String, converterDescription: String,
                            regressionErrors: List[RegressionError])

  case class TaskError(taskName: String, converterErrors: List[ConverterError])

}
