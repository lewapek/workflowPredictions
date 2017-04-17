package pl.edu.agh.workflowPerformance.workflows.logs.sns

import com.typesafe.scalalogging.StrictLogging
import pl.edu.agh.workflowPerformance.Settings

import scala.io.Source

/**
  * @author lewap
  * @since 23.03.17
  */
object SnsNeuralNetworkStatsRunner extends Settings with StrictLogging {

  val snsNeuralNetworkResultsDirectory = resourcesData("snsWorkflows/nnResults")

  def main(args: Array[String]): Unit = {

    val file = snsNeuralNetworkResultsDirectory + "/namd_stime.csv"
    val lines = Source.fromFile(file).getLines().toList.tail
    val realVsPredictedLines = lines map { line =>
      val split = line.split(',')
      val real = split(8).toDouble
      val predicted = split(9).toDouble
      real -> predicted
    }

    val size = realVsPredictedLines.size
    var (rmse, relativeDivMean, relative) = (0.0, 0.0, 0.0)
    realVsPredictedLines foreach { case (real, predicted) =>
      val difference = (real - predicted).abs
      rmse += difference * difference
      relative += difference / real
    }

    rmse = Math.sqrt(rmse / size)
    relative /= size
    logger.info(s"RMSE = $rmse, relative = $relative")
  }

}
