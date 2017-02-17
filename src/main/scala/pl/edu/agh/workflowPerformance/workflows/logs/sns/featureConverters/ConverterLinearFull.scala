package pl.edu.agh.workflowPerformance.workflows.logs.sns.featureConverters

import pl.edu.agh.workflowPerformance.workflows.logs.AbstractFeatureConverter
import pl.edu.agh.workflowPerformance.workflows.logs.sns.structure.SnsRow

/**
  * @author lewap
  * @since 14.02.17
  */
object ConverterLinearFull extends AbstractFeatureConverter[SnsRow] {
  override val description: String =
    "atoms :: cores :: timesteps :: outputFrequency"

  override def convert(row: SnsRow): List[AnyVal] = {
    import row._
    atoms :: cores :: timesteps :: outputFrequency :: Nil
  }
}
