package pl.edu.agh.workflowPerformance.workflows.logs.regression

import com.typesafe.scalalogging.StrictLogging
import pl.edu.agh.workflowPerformance.workflows._

import scala.io.Source
import scala.language.postfixOps

/**
  * @author lewap
  * @since 23.04.17
  */
trait StatsUtils extends StrictLogging {

  def calculateErrorsFrom(comparisonFile: String): Errors = {
    val realVsPredicted = Source.fromFile(comparisonFile).getLines() map { line =>
      val split = line.split(',')
      val real = split(0).toDouble
      val predicted = split(1).toDouble
      real -> predicted
    } toList

    calculateErrorsFrom(realVsPredicted)
  }

  def calculateErrorsFrom(realVsPredicted: List[(Double, Double)]): Errors = {
    val size = realVsPredicted.size
    var (rmse, absoluteDivMean, relative) = (0.0, 0.0, 0.0)
    var realSum = 0.0
    realVsPredicted foreach { case (real, predicted) =>
      realSum += real

      val difference = (real - predicted).abs
      absoluteDivMean += difference
      rmse += difference * difference
      relative += difference / real
    }

    rmse = Math.sqrt(rmse / size)
    absoluteDivMean = absoluteDivMean / realSum
    relative /= size
    logger.info(s"RMSE = $rmse, absDivMean = $absoluteDivMean, relative = $relative")
    Errors(rmse, absoluteDivMean, relative)
  }

}
