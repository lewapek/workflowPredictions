package pl.edu.agh.workflowPerformance.utils

import scala.io.Source
import scala.language.postfixOps
import scala.sys.process._

/**
  * @author lewap
  * @since 08.01.17
  */
object ExternalRegressionExecutor {

  type RegressionFunction = String => Double

  private val octavePrefix = "src/main/octave"
  private val linearRegressionPrefix = octavePrefix + "/linearRegression"
  private val decisionTreeRegressionPrefix = "src/main/python3"

  def runNormalEquationsWith(inputFilename: String): Double = {
    s"octave $linearRegressionPrefix/workflowsNormalEquations.m $inputFilename".!
    rmse
  }

  def runLinearRegressionGradientDescentWith(inputFilename: String): Double = {
    s"octave $linearRegressionPrefix/workflowsLinearRegression.m $inputFilename".!
    rmse
  }

  def runRegressionDecisionTreeWith(treeMaxDepth: Int = 10)(inputFilename: String): Double = {
    s"python3 $decisionTreeRegressionPrefix/regression_decision_tree.py -m $treeMaxDepth -i $inputFilename".!
    rmse
  }

  def runRegressionNearestNeighbourWith(neighboursNumber: Int = 10)(inputFilename: String): Double = {
    s"python3 $decisionTreeRegressionPrefix/regression_nearest_neighbours.py -n $neighboursNumber -i $inputFilename".!
    rmse
  }

  def rmse: Double = {
    doubleFrom(Source.fromFile("tmp/rmse.csv").getLines().toList.head)
  }

  def absoluteErrorDivMean: Double = {
    doubleFrom(Source.fromFile("tmp/absDivMean.csv").getLines().toList.head)
  }

  def relativeError: Double = {
    doubleFrom(Source.fromFile("tmp/relativeError.csv").getLines().toList.head)
  }

  def theta: List[Double] = {
    Source.fromFile("tmp/theta.csv").getLines().map(_.toDouble).toList
  }

  private def doubleFrom(string: String): Double = {
    if (string.toLowerCase() == "inf") {
      -1.0
    } else string.toDouble
  }

}
