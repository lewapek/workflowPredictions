package pl.edu.agh.workflowPerformance.workflows.logs.sns

import com.typesafe.scalalogging.StrictLogging
import pl.edu.agh.workflowPerformance.Settings
import pl.edu.agh.workflowPerformance.workflows.logs.regression.StatsUtils
import pl.edu.agh.workflowPerformance.workflows._

import scala.io.Source

/**
  * @author lewap
  * @since 23.03.17
  */
object SnsNeuralNetworkStatsRunner extends StatsUtils with Settings with StrictLogging {

  val snsNeuralNetworkResultsDirectory = resourcesData("snsWorkflows/nnResults")
  val prefixes = List("namd", "sasena")
  val suffixes = List("stime", "utime", "write", "read")
  val files = prefixes.flatMap(prefix => suffixes.map(suffix => prefix + "_" + suffix + ".csv"))

  def main(args: Array[String]): Unit = {

    val stats = files.foldLeft(Map.empty[String, Errors]) { (map, filename) =>
      val errors = calculateSingle(filename)
      map + (filename -> errors)
    }

    println(stats.mkString("\n"))

  }

  def calculateSingle(filename: String): Errors = {
    val file = snsNeuralNetworkResultsDirectory + "/" + filename
    val lines = Source.fromFile(file).getLines().toList.tail
    val realVsPredictedLines = lines map { line =>
      val split = line.split(',')
      val real = split(8).toDouble
      val predicted = split(9).toDouble
      real -> predicted
    }

    calculateErrorsFrom(realVsPredictedLines)
  }

}
