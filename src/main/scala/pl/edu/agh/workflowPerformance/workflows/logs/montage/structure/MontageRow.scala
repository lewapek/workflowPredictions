package pl.edu.agh.workflowPerformance.workflows.logs.montage.structure

import pl.edu.agh.workflowPerformance.workflows.montage8Yaml.Entities.Instance
import pl.edu.agh.workflowPerformance.workflows.logs.regression.AbstractRow

/**
  * @author lewap
  * @since 08.01.17
  */
case class MontageRow(time: Double,
                      performance: Double,
                      instance: Instance,
                      montage: Double,
                      inputDataSize: Long,
                      outputDataSize: Long) extends AbstractRow {

  def asFeatureVectorWithTime: List[AnyVal] = {
    time :: instance.asFeatureVector ::: montage :: inputDataSize :: outputDataSize :: Nil
  }

  def asFeatureVectorMontageSquareWithTime: List[AnyVal] = {
    time :: instance.asFeatureVector ::: (montage * montage) :: inputDataSize :: outputDataSize :: Nil
  }

  def asFeatureVectorWithPerformance: List[AnyVal] = {
    performance :: instance.asFeatureVector ::: montage :: inputDataSize :: Nil
  }

  def asFeatureVectorWithPerformanceMulCores: List[AnyVal] = {
    (performance * instance.cores) :: instance.asFeatureVector ::: montage :: inputDataSize :: Nil
  }

  def asFeatureVectorMontageSquareWithPerformance: List[AnyVal] = {
    performance :: instance.asFeatureVector ::: (montage * montage) :: inputDataSize :: Nil
  }

}
