package pl.edu.agh.workflowPerformance.utils

import scala.io.Source
import scala.language.postfixOps
import scala.sys.process._

/**
  * @author lewap
  * @since 08.01.17
  */
object Octave {

  type RegressionFunction = String => Double

  private val octavePrefix = "src/main/octave"
  private val linearRegressionPrefix = octavePrefix + "/linearRegression"

  def runNormalEquationsWith(inputFilename: String): Double = {
    s"octave $linearRegressionPrefix/workflowsNormalEquations.m $inputFilename".!
    rmse
  }

  def runLinearRegressionGradientDescentWith(inputFilename: String): Double = {
    s"octave $linearRegressionPrefix/workflowsLinearRegression.m $inputFilename".!
    rmse
  }

  def rmse: Double = {
    doubleFrom(Source.fromFile("tmp/rmse.csv").getLines().toList.head)
  }

  def relativeError: Double = {
    doubleFrom(Source.fromFile("tmp/relativeError.csv").getLines().toList.head)
  }

  def theta: List[Double] = {
    Source.fromFile("tmp/theta.csv").getLines().map(_.toDouble).toList
  }

  private def doubleFrom(string: String): Double = {
    if (string == "Inf" || string == "inf") {
      Double.MaxValue
    } else string.toDouble
  }

}