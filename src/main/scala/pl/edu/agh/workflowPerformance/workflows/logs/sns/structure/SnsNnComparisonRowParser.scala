package pl.edu.agh.workflowPerformance.workflows.logs.sns.structure

/**
  * @author lewap
  * @since 16.02.17
  */
trait SnsNnComparisonRowParser {

  def parseSnsProfileRowWithStime(line: String): SnsProfileRow = {
    val parsed = parse(line)
    import parsed._
    SnsProfileRow(y = stime, atoms, cores, timesteps, outputFrequency, pointInTime)
  }

  def parseSnsProfileRowWithUtime(line: String): SnsProfileRow = {
    val parsed = parse(line)
    import parsed._
    SnsProfileRow(y = utime, atoms, cores, timesteps, outputFrequency, pointInTime)
  }

  def parseSnsProfileRowWithWriteBytes(line: String): SnsProfileRow = {
    val parsed = parse(line)
    import parsed._
    SnsProfileRow(y = writeBytes, atoms, cores, timesteps, outputFrequency, pointInTime)
  }

  def parseSnsProfileRowWithReadBytes(line: String): SnsProfileRow = {
    val parsed = parse(line)
    import parsed._
    SnsProfileRow(y = readBytes, atoms, cores, timesteps, outputFrequency, pointInTime)
  }

  private def parse(line: String) = new {
    val splitLine = line.trim.split(',')

    val atoms = splitLine(0).toDouble
    val cores = splitLine(1).toDouble
    val timesteps = splitLine(2).toDouble
    val outputFrequency = splitLine(3).toDouble

    val stime = splitLine(4).toDouble
    val utime = splitLine(5).toDouble
    val writeBytes = splitLine(6).toDouble
    val readBytes = splitLine(7).toDouble

    val pointInTime = splitLine(8).toDouble
  }

}
