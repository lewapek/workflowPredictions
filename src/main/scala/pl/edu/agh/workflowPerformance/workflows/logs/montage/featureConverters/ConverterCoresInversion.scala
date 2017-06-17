package pl.edu.agh.workflowPerformance.workflows.logs.montage.featureConverters

import pl.edu.agh.workflowPerformance.workflows.logs.montage.structure.MontageRow
import pl.edu.agh.workflowPerformance.workflows.logs.regression.AbstractFeatureConverter

/**
  * @author lewap
  * @since 14.02.17
  */
object ConverterCoresInversion extends AbstractFeatureConverter[MontageRow] {
  override def shortNameNoCommas: String = "1/c"

  override val description: String =
    "price :: (1.0 / cores) :: memoryGiB :: network :: montage :: inputDataSize :: outputDataSize :: Nil"

  override def convert(row: MontageRow): List[AnyVal] = {
    import row._
    import row.instance._
    price :: (1.0 / cores) :: memoryGiB :: network :: montage :: inputDataSize :: outputDataSize :: Nil
  }
}
