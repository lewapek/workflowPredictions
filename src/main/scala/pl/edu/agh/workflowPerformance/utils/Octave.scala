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
    Source.fromFile("tmp/rmse.csv").getLines().toList.head.toDouble
  }

  def relativeError: Double = {
    Source.fromFile("tmp/relativeError.csv").getLines().toList.head.toDouble
  }

  def theta: List[Double] = {
    Source.fromFile("tmp/theta.csv").getLines().map(_.toDouble).toList
  }

}
