package pl.edu.agh.workflowPerformance

/**
  * @author lewap
  * @since 04.12.16
  */
object Settings extends Settings

trait Settings {

  def resourcesData(name: String): String =
    "src/main/resources/data/" + name

  def resultFile(name: String): String =
    "results/" + name

  def tmpFile(name: String): String =
    "tmp/" + name

}
