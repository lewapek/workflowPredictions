package pl.edu.agh.workflowPerformance.workflows.logs.regression

import pl.edu.agh.workflowPerformance.utils.ExternalRegressionExecutor._
import pl.edu.agh.workflowPerformance.workflows.Errors

/**
  * @author lewap
  * @since 26.02.17
  */
object Regressions {

  def normalEquations(runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = "ne"
    override val runs: Int = 1

    override def function(inputFilename: String, splitFactor: Double, split: Option[Int] = None): Errors = {
      runNormalEquationsWith(inputFilename, splitFactor, split)
      Errors(rmse, mae, absoluteErrorDivMean, relativeError)
    }
  }

  def gradientDescent(runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = "gd"
    override val runs: Int = runsQuantity

    override def function(inputFilename: String, splitFactor: Double, split: Option[Int] = None): Errors = {
      runLinearRegressionGradientDescentWith(inputFilename, splitFactor, split)
      Errors(rmse, mae, absoluteErrorDivMean, relativeError)
    }
  }

  def decisionTree(maxDepth: Int = 10, runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = s"dt_$maxDepth"
    override val runs: Int = runsQuantity

    override def function(inputFilename: String, splitFactor: Double, split: Option[Int] = None): Errors = {
      runRegressionDecisionTreeWith(treeMaxDepth = maxDepth)(inputFilename, splitFactor, split)
      Errors(rmse, mae, absoluteErrorDivMean, relativeError)
    }
  }

  def randomForest(maxDepth: Int = 10, estimatorsNumber: Int = 10, runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = s"rf_${estimatorsNumber}_$maxDepth"
    override val runs: Int = runsQuantity

    override def function(inputFilename: String, splitFactor: Double, split: Option[Int] = None): Errors = {
      runRegressionRandomForestWith(treeMaxDepth = maxDepth, estimatorsNumber = estimatorsNumber)(inputFilename, splitFactor, split)
      Errors(rmse, mae, absoluteErrorDivMean, relativeError)
    }
  }

  def extraTrees(maxDepth: Int = 10, estimatorsNumber: Int = 10, runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = s"et_${estimatorsNumber}_$maxDepth"
    override val runs: Int = runsQuantity

    override def function(inputFilename: String, splitFactor: Double, split: Option[Int] = None): Errors = {
      runRegressionExtraTreesWith(treeMaxDepth = maxDepth, estimatorsNumber = estimatorsNumber)(inputFilename, splitFactor, split)
      Errors(rmse, mae, absoluteErrorDivMean, relativeError)
    }
  }

  def adaBoosting(maxDepth: Int = 10, estimatorsNumber: Int = 50, runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = s"ab_${estimatorsNumber}_$maxDepth"
    override val runs: Int = runsQuantity

    override def function(inputFilename: String, splitFactor: Double, split: Option[Int] = None): Errors = {
      runRegressionAdaBoostingWith(treeMaxDepth = maxDepth, estimatorsNumber = estimatorsNumber)(inputFilename, splitFactor, split)
      Errors(rmse, mae, absoluteErrorDivMean, relativeError)
    }
  }

  def stochasticGradientBoosting(estimatorsNumber: Int = 100, runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = s"sgb_$estimatorsNumber"
    override val runs: Int = runsQuantity

    override def function(inputFilename: String, splitFactor: Double, split: Option[Int] = None): Errors = {
      runRegressionStochasticGradientBoostingWith(estimatorsNumber)(inputFilename, splitFactor, split)
      Errors(rmse, mae, absoluteErrorDivMean, relativeError)
    }
  }

  def nearestNeighbours(neighboursNumber: Int = 10,
                        algorithm: NearestNeighbourAlgorithms.Algorithm = NearestNeighbourAlgorithms.Auto,
                        runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = s"nb_${neighboursNumber}_${algorithm.name}"
    override val runs: Int = runsQuantity

    override def function(inputFilename: String, splitFactor: Double, split: Option[Int] = None): Errors = {
      runRegressionNearestNeighbourWith(neighboursNumber, algorithm.name)(inputFilename, splitFactor, split)
      Errors(rmse, mae, absoluteErrorDivMean, relativeError)
    }
  }

  def svm(kernel: SvmKernels.Kernel = SvmKernels.Rbf,
          c: Double = 1.0,
          epsilon: Double = 0.1,
          runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = s"svm_${kernel.name}_${c}_$epsilon"
    override val runs: Int = runsQuantity

    override def function(inputFilename: String, splitFactor: Double, split: Option[Int] = None): Errors = {
      runRegressionSvmWith(kernel.name, c, epsilon)(inputFilename, splitFactor, split)
      Errors(rmse, mae, absoluteErrorDivMean, relativeError)
    }
  }

  def multilayerPerceptron(layers: Int,
                           layerSize: Int,
                           maxIterations: Int = 2000,
                           solver: Mlp.Solver = Mlp.Lbfgs,
                           runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = s"mlp_${layers}_${layerSize}_${maxIterations}_${solver.name}"
    override val runs: Int = runsQuantity

    override def function(inputFilename: String, splitFactor: Double, split: Option[Int] = None): Errors = {
      runRegressionMlpWith(layers, layerSize, maxIterations, solver.name)(inputFilename, splitFactor, split)
      Errors(rmse, mae, absoluteErrorDivMean, relativeError)
    }
  }

}

object NearestNeighbourAlgorithms {

  sealed abstract class Algorithm(val name: String)

  case object Auto extends Algorithm("auto")

  case object BallTree extends Algorithm("ball_tree")

  case object KdTree extends Algorithm("kd_tree")

}

object SvmKernels {

  sealed abstract class Kernel(val name: String)

  case object Linear extends Kernel("linear")

  case object Polynomial extends Kernel("poly")

  case object Rbf extends Kernel("rbf")

  case object Sigmoid extends Kernel("sigmoid")

}

object Mlp {

  sealed abstract class Solver(val name: String)

  case object Lbfgs extends Solver("lbfgs")

  case object Sgd extends Solver("sgd")

  case object Adam extends Solver("adam")

}
