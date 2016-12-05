package pl.edu.agh.workflowPerformance

/**
  * @author lewap
  * @since 04.12.16
  */
object Settings extends Settings {

  val dataPrefix = "src/main/resources/data"
  val dataFile = tmpFile("workflowData.csv")
  val thetaPath = tmpFile("theta.csv")
  val rootMeanSquareErrorPath = tmpFile("rmse.csv")

}

trait Settings {

  def resultFile(name: String): String =
    "results/" + name

  def tmpFile(name: String): String =
    "tmp/" + name

}
