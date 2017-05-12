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
    var (squaredErrorsSum, absoluteErrorsSum, relativeErrorsSum) = (0.0, 0.0, 0.0)
    var realSum = 0.0
    realVsPredicted foreach { case (real, predicted) =>
      realSum += real

      val difference = (real - predicted).abs
      absoluteErrorsSum += difference
      squaredErrorsSum += difference * difference
      relativeErrorsSum += difference / real
    }

    val rmse = Math.sqrt(squaredErrorsSum / size)
    val mae = absoluteErrorsSum / size
    val absDivMean = absoluteErrorsSum / realSum
    val relative = relativeErrorsSum / size
    logger.info(s"RMSE = $rmse, mae = $mae, absDivMean = $absDivMean, relative = $relative")
    Errors(rmse, mae, absDivMean, relative)
  }

}
