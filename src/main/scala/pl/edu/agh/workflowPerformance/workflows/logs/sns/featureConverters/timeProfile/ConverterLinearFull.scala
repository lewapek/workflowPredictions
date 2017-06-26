package pl.edu.agh.workflowPerformance.workflows.logs.sns.featureConverters.timeProfile

import pl.edu.agh.workflowPerformance.workflows.logs.regression.AbstractFeatureConverter
import pl.edu.agh.workflowPerformance.workflows.logs.sns.structure.SnsProfileRow

/**
  * @author lewap
  * @since 14.02.17
  */
object ConverterLinearFull extends AbstractFeatureConverter[SnsProfileRow] {

  override val name: String = ""

  override val description: String =
    "atoms :: cores :: timesteps :: outputFrequency :: pointInTime"

  override def convert(row: SnsProfileRow): List[AnyVal] = {
    import row._
    atoms :: cores :: timesteps :: outputFrequency :: pointInTime :: Nil
  }
}

object A extends App {
  import reflect.runtime._

  println(currentMirror)
  println(currentMirror.staticModule("scala.Array"))
  println(currentMirror.reflectModule(currentMirror.staticModule("scala.Array")))
  println(currentMirror.reflectModule(currentMirror
    .staticModule("pl.edu.agh.workflowPerformance.workflows.logs.sns.featureConverters.timeProfile.ConverterLinearFull"))
    .instance.asInstanceOf[AbstractFeatureConverter[SnsProfileRow]].description
  )
}