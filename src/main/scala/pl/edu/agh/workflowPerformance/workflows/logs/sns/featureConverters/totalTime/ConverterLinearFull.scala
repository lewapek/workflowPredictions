package pl.edu.agh.workflowPerformance.workflows.logs.sns.featureConverters.totalTime

import pl.edu.agh.workflowPerformance.workflows.logs.regression.AbstractFeatureConverter
import pl.edu.agh.workflowPerformance.workflows.logs.sns.structure.SnsTotalTimeRow

/**
  * @author lewap
  * @since 14.02.17
  */
object ConverterLinearFull extends AbstractFeatureConverter[SnsTotalTimeRow] {
  override val description: String =
    "atoms :: cores :: timesteps :: outputFrequency"

  override def convert(row: SnsTotalTimeRow): List[AnyVal] = {
    import row._
    atoms :: cores :: timesteps :: outputFrequency :: Nil
  }
}
