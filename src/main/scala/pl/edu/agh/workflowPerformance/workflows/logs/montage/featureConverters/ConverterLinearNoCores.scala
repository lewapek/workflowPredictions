package pl.edu.agh.workflowPerformance.workflows.logs.montage.featureConverters

import pl.edu.agh.workflowPerformance.workflows.logs.montage.structure.MontageRow
import pl.edu.agh.workflowPerformance.workflows.logs.regression.AbstractFeatureConverter

/**
  * @author lewap
  * @since 14.02.17
  */

object ConverterLinearNoCores extends AbstractFeatureConverter[MontageRow] {
  override val description: String =
    "price :: memoryGiB :: network :: montage :: inputDataSize :: outputDataSize"

  override def convert(row: MontageRow): List[AnyVal] = {
    import row._
    import row.instance._
    price :: memoryGiB :: network :: montage :: inputDataSize :: outputDataSize :: Nil
  }
}
