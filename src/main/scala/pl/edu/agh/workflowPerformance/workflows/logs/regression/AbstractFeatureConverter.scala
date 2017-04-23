package pl.edu.agh.workflowPerformance.workflows.logs.regression

/**
  * @author lewap
  * @since 14.02.17
  */
abstract class AbstractFeatureConverter[T <: AbstractRow] {

  def name: String =
    getClass.getSimpleName.replaceAll("\\$", "")

  val description: String

  def convertWithTime(row: T): List[AnyVal] =
    row.y :: convert(row)

  def convert(row: T): List[AnyVal]

}
