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
  private val pythonRegressionsPrefix = "src/main/python3"

  def runNormalEquationsWith(inputFilename: String, splitFactor: Double = 0.8, split: Option[Int] = None): Double = {
    s"octave $linearRegressionPrefix/workflowsNormalEquations.m $inputFilename $splitFactor".!
    rmse
  }

  def runLinearRegressionGradientDescentWith(inputFilename: String,
                                             splitFactor: Double = 0.8,
                                             split: Option[Int] = None): Double = {
    s"octave $linearRegressionPrefix/workflowsLinearRegression.m $inputFilename $splitFactor".!
    rmse
  }

  def runRegressionDecisionTreeWith(treeMaxDepth: Int)
                                   (inputFilename: String,
                                    splitFactor: Double = 0.8,
                                    split: Option[Int] = None): Double = {
    split.fold {
      s"python3 $pythonRegressionsPrefix/regression_decision_tree.py -f $splitFactor -m $treeMaxDepth -i $inputFilename".!
    } { splitParam =>
      s"python3 $pythonRegressionsPrefix/regression_decision_tree.py -f $splitFactor -m $treeMaxDepth -i $inputFilename -s $splitParam".!
    }
    rmse
  }

  def runRegressionRandomForestWith(treeMaxDepth: Int)
                                   (inputFilename: String,
                                    splitFactor: Double = 0.8,
                                    split: Option[Int] = None): Double = {
    split.fold {
      s"python3 $pythonRegressionsPrefix/regression_random_forest.py -f $splitFactor -m $treeMaxDepth -i $inputFilename".!
    } { splitParam =>
      s"python3 $pythonRegressionsPrefix/regression_random_forest.py -f $splitFactor -m $treeMaxDepth -i $inputFilename -s $splitParam".!
    }
    rmse
  }

  def runRegressionExtraTreesWith(treeMaxDepth: Int)
                                 (inputFilename: String,
                                  splitFactor: Double = 0.8,
                                  split: Option[Int] = None): Double = {
    split.fold {
      s"python3 $pythonRegressionsPrefix/regression_extra_trees.py -f $splitFactor -m $treeMaxDepth -i $inputFilename".!
    } { splitParam =>
      s"python3 $pythonRegressionsPrefix/regression_extra_trees.py -f $splitFactor -m $treeMaxDepth -i $inputFilename -s $splitParam".!
    }
    rmse
  }

  def runRegressionAdaBoostingWith(treeMaxDepth: Int)
                                  (inputFilename: String,
                                   splitFactor: Double = 0.8,
                                   split: Option[Int] = None): Double = {
    split.fold {
      s"python3 $pythonRegressionsPrefix/regression_ada_boosting.py -f $splitFactor -m $treeMaxDepth -i $inputFilename".!
    } { splitParam =>
      s"python3 $pythonRegressionsPrefix/regression_ada_boosting.py -f $splitFactor -m $treeMaxDepth -i $inputFilename -s $splitParam".!
    }
    rmse
  }

  def runRegressionStochasticGradientBoostingWith()
                                                 (inputFilename: String,
                                                  splitFactor: Double = 0.8,
                                                  split: Option[Int] = None): Double = {
    split.fold {
      s"python3 $pythonRegressionsPrefix/regression_stochastic_gradient_boosting.py -f $splitFactor -i $inputFilename".!
    } { splitParam =>
      s"python3 $pythonRegressionsPrefix/regression_stochastic_gradient_boosting.py -f $splitFactor -i $inputFilename -s $splitParam".!
    }
    rmse
  }

  def runRegressionNearestNeighbourWith(neighboursNumber: Int,
                                        algorithm: String)
                                       (inputFilename: String,
                                        splitFactor: Double = 0.8,
                                        split: Option[Int] = None): Double = {
    split.fold {
      s"python3 $pythonRegressionsPrefix/regression_nearest_neighbours.py -f $splitFactor -n $neighboursNumber -a $algorithm -i $inputFilename".!
    } { splitParam =>
      s"python3 $pythonRegressionsPrefix/regression_nearest_neighbours.py -f $splitFactor -n $neighboursNumber -a $algorithm -i $inputFilename -s $splitParam".!
    }
    rmse
  }

  def runRegressionSvmWith(kernel: String, c: Double, epsilon: Double)
                          (inputFilename: String, splitFactor: Double = 0.8, split: Option[Int] = None): Double = {
    split.fold {
      s"python3 $pythonRegressionsPrefix/regression_svm.py -f $splitFactor -k $kernel -C $c -e $epsilon -i $inputFilename".!
    } { splitParam =>
      s"python3 $pythonRegressionsPrefix/regression_svm.py -f $splitFactor -k $kernel -C $c -e $epsilon -i $inputFilename -s $splitParam".!
    }
    rmse
  }

  def runRegressionMlpWith(layers: Int, layerSize: Int, maxIterations: Int, solver: String)
                          (inputFilename: String, splitFactor: Double = 0.8, split: Option[Int] = None): Double = {
    split.fold {
      s"python3 $pythonRegressionsPrefix/regression_mlp.py -f $splitFactor -l $layers -S $layerSize -m $maxIterations -a $solver -i $inputFilename".!
    } { splitParam =>
      s"python3 $pythonRegressionsPrefix/regression_mlp.py -f $splitFactor -l $layers -S $layerSize -m $maxIterations -a $solver -i $inputFilename -s $splitParam".!
    }
    rmse
  }

  def rmse: Double =
    doubleFrom(Source.fromFile("tmp/rmse.csv").getLines().toList.head)

  def mae: Double =
    doubleFrom(Source.fromFile("tmp/mae.csv").getLines().toList.head)

  def absoluteErrorDivMean: Double =
    doubleFrom(Source.fromFile("tmp/absDivMean.csv").getLines().toList.head)

  def relativeError: Double =
    doubleFrom(Source.fromFile("tmp/relativeError.csv").getLines().toList.head)

  def theta: List[Double] =
    Source.fromFile("tmp/theta.csv").getLines().map(_.toDouble).toList

  private def doubleFrom(string: String): Double = {
    if (string.toLowerCase() == "inf") {
      -1.0
    } else string.toDouble
  }

}
