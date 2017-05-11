package pl.edu.agh.workflowPerformance.workflows.logs.montage.featureConverters

import pl.edu.agh.workflowPerformance.workflows.logs.montage.structure.MontageRow
import pl.edu.agh.workflowPerformance.workflows.logs.regression.AbstractFeatureConverter

/**
  * @author lewap
  * @since 14.02.17
  */
object ConverterLinearNoNetwork extends AbstractFeatureConverter[MontageRow] {
  override val description: String =
    "price :: cores :: memoryGiB :: montage :: inputDataSize :: outputDataSize"

  override def convert(row: MontageRow): List[AnyVal] = {
    import row._
    import row.instance._
    price :: cores :: memoryGiB :: montage :: inputDataSize :: outputDataSize :: Nil
  }
}
