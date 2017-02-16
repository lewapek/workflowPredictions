package pl.edu.agh.workflowPerformance.workflows.logs.montage.featureConverters

import pl.edu.agh.workflowPerformance.workflows.logs.AbstractFeatureConverter
import pl.edu.agh.workflowPerformance.workflows.logs.montage.structure.MontageRow

/**
  * @author lewap
  * @since 14.02.17
  */
object ConverterLinearNoInstance extends AbstractFeatureConverter[MontageRow] {
  override val description: String =
    "montage :: inputDataSize :: outputDataSize"

  override def convert(row: MontageRow): List[AnyVal] = {
    import row._
    montage :: inputDataSize :: outputDataSize :: Nil
  }
}
