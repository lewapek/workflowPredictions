package pl.edu.agh.workflowPerformance.workflows.logs.sns.structure

/**
  * @author lewap
  * @since 16.02.17
  */
trait SnsRowParser {
  def parseSnsRow(line: String): SnsRow = {
    val splitLine = line.trim.split(',')

    val atoms = Atoms.from(splitLine(0))
    val cores = Cores.from(splitLine(1))
    val timesteps = Timesteps.from(splitLine(2))
    val outputFrequency = OutputFrequencies.from(splitLine(3))
    val time = splitLine(6).toDouble

    SnsRow(time, atoms, cores, timesteps, outputFrequency)
  }
}

object Atoms {
  val values = Map[String, Double]("L" -> 3692, "M" -> 5580.5, "H" -> 7492)

  def from(string: String): Double =
    values(string)
}

object Cores {
  val values = Map[String, Long]("L" -> 144, "M" -> 216, "H" -> 288)

  def from(string: String): Long =
    values(string)
}

object Timesteps {
  val values = Map[String, Double]("L" -> 50000, "M" -> 275000, "H" -> 500000)

  def from(string: String): Double =
    values(string)
}

object OutputFrequencies {
  val values = Map[String, Double]("L" -> 1000, "M" -> 3000, "H" -> 5000)

  def from(string: String): Double =
    values(string)
}
