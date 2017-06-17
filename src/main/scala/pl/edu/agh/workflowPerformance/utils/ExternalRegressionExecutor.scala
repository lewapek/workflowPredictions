package pl.edu.agh.workflowPerformance.utils

import scala.io.Source
import scala.language.postfixOps
import scala.sys.process._

/**
  * @author lewap
  * @since 08.01.17
  */
object ExternalRegressionExecutor {

  private val octavePrefix = "src/main/octave"
  private val linearRegressionPrefix = octavePrefix + "/linearRegression"
  private val pythonRegressionsPrefix = "src/main/python3"

  def runNormalEquationsWith(inputFilename: String, splitFactor: Double = 0.8, split: Option[Int] = None): Unit = {
    s"octave $linearRegressionPrefix/workflowsNormalEquations.m $inputFilename $splitFactor" !
  }

  def runLinearRegressionGradientDescentWith(inputFilename: String,
                                             splitFactor: Double = 0.8,
                                             split: Option[Int] = None): Unit = {
    s"octave $linearRegressionPrefix/workflowsLinearRegression.m $inputFilename $splitFactor" !
  }

  def runRegressionDecisionTreeWith(treeMaxDepth: Int)
                                   (inputFilename: String,
                                    splitFactor: Double = 0.8,
                                    split: Option[Int] = None): Unit = {
    runPythonRegressionUsing(
      "regression_decision_tree.py",
      inputFilename,
      splitFactor,
      split,
      otherArguments = s"-m $treeMaxDepth"
    )
  }

  def runRegressionRandomForestWith(treeMaxDepth: Int,
                                    estimatorsNumber: Int = 10)
                                   (inputFilename: String,
                                    splitFactor: Double = 0.8,
                                    split: Option[Int] = None): Unit = {
    runPythonRegressionUsing(
      "regression_random_forest.py",
      inputFilename,
      splitFactor,
      split,
      otherArguments = s"-m $treeMaxDepth -n $estimatorsNumber"
    )
  }

  def runRegressionExtraTreesWith(treeMaxDepth: Int,
                                  estimatorsNumber: Int = 10)
                                 (inputFilename: String,
                                  splitFactor: Double = 0.8,
                                  split: Option[Int] = None): Unit = {
    runPythonRegressionUsing(
      "regression_extra_trees.py",
      inputFilename,
      splitFactor,
      split,
      otherArguments = s"-m $treeMaxDepth -n $estimatorsNumber"
    )
  }

  def runRegressionAdaBoostingWith(treeMaxDepth: Int,
                                   estimatorsNumber: Int = 50)
                                  (inputFilename: String,
                                   splitFactor: Double = 0.8,
                                   split: Option[Int] = None): Unit = {
    runPythonRegressionUsing(
      "regression_ada_boosting.py",
      inputFilename,
      splitFactor,
      split,
      otherArguments = s"-m $treeMaxDepth -n $estimatorsNumber"
    )
  }

  def runRegressionStochasticGradientBoostingWith(estimatorsNumber: Int = 100)
                                                 (inputFilename: String,
                                                  splitFactor: Double = 0.8,
                                                  split: Option[Int] = None): Unit = {
    runPythonRegressionUsing(
      "regression_stochastic_gradient_boosting.py",
      inputFilename,
      splitFactor,
      split,
      otherArguments = s"-n $estimatorsNumber"
    )
  }

  def runRegressionNearestNeighbourWith(neighboursNumber: Int,
                                        algorithm: String)
                                       (inputFilename: String,
                                        splitFactor: Double = 0.8,
                                        split: Option[Int] = None): Unit = {
    runPythonRegressionUsing(
      "regression_nearest_neighbours.py",
      inputFilename,
      splitFactor,
      split,
      otherArguments = s"-n $neighboursNumber -a $algorithm"
    )
  }

  def runRegressionSvmWith(kernel: String,
                           c: Double,
                           epsilon: Double)
                          (inputFilename: String,
                           splitFactor: Double = 0.8,
                           split: Option[Int] = None): Unit = {
    runPythonRegressionUsing(
      "regression_svm.py",
      inputFilename,
      splitFactor,
      split,
      otherArguments = s"-k $kernel -C $c -e $epsilon"
    )
  }

  def runRegressionMlpWith(layers: Int,
                           layerSize: Int,
                           maxIterations: Int,
                           solver: String)
                          (inputFilename: String,
                           splitFactor: Double = 0.8,
                           split: Option[Int] = None): Unit = {
    runPythonRegressionUsing(
      "regression_mlp.py",
      inputFilename,
      splitFactor,
      split,
      otherArguments = s"-l $layers -S $layerSize -m $maxIterations -a $solver"
    )
  }

  private def runPythonRegressionUsing(name: String,
                                       inputFilename: String,
                                       splitFactor: Double,
                                       split: Option[Int],
                                       otherArguments: String): Unit = {
    val inputFilenameArgument = "-i " + inputFilename
    val splitFactorArgument = "-f " + splitFactor
    val splitArgument = splitArgumentFrom(split)
    val allArguments = concatenateArguments(inputFilenameArgument, splitFactorArgument, splitArgument, otherArguments)

    val commandPrefix = s"python3 $pythonRegressionsPrefix/$name "
    val command = commandPrefix + allArguments

    command !
  }

  private def splitArgumentFrom(split: Option[Int]): String =
    split.map("-s " + _).getOrElse("")

  private def concatenateArguments(arguments: String*): String =
    arguments.mkString(" ")

  def rmse: Double =
    doubleFromFile("tmp/rmse.csv")

  def mae: Double =
    doubleFromFile("tmp/mae.csv")

  def absoluteErrorDivMean: Double =
    doubleFromFile("tmp/absDivMean.csv")

  def relativeError: Double =
    doubleFromFile("tmp/relativeError.csv")

  def theta: List[Double] =
    Source.fromFile("tmp/theta.csv").getLines().map(_.toDouble).toList

  private def doubleFromFile(path: String): Double =
    doubleFromStringRepresentation(Source.fromFile(path).getLines().toList.head)

  private def doubleFromStringRepresentation(string: String): Double = {
    if (string.toLowerCase() == "inf") {
      -1.0
    } else string.toDouble
  }

}
