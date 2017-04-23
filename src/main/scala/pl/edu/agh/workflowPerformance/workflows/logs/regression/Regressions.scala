package pl.edu.agh.workflowPerformance.workflows.logs.regression

import pl.edu.agh.workflowPerformance.utils.ExternalRegressionExecutor._
import pl.edu.agh.workflowPerformance.workflows.Errors

/**
  * @author lewap
  * @since 26.02.17
  */
object Regressions {

  def normalEquations(runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = "normalEquations"
    override val runs: Int = 1

    override def function(inputFilename: String, split: Option[Int] = None): Errors = {
      runNormalEquationsWith(inputFilename, split)
      Errors(rmse, absoluteErrorDivMean, relativeError)
    }
  }

  def gradientDescent(runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = "gradientDescent"
    override val runs: Int = runsQuantity

    override def function(inputFilename: String, split: Option[Int] = None): Errors = {
      runLinearRegressionGradientDescentWith(inputFilename, split)
      Errors(rmse, absoluteErrorDivMean, relativeError)
    }
  }

  def decisionTree(maxDepth: Int = 10, runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = s"decisionTree_$maxDepth"
    override val runs: Int = runsQuantity

    override def function(inputFilename: String, split: Option[Int] = None): Errors = {
      runRegressionDecisionTreeWith(treeMaxDepth = maxDepth)(inputFilename, split)
      Errors(rmse, absoluteErrorDivMean, relativeError)
    }
  }

  def nearestNeighbours(neighboursNumber: Int = 10,
                        algorithm: NearestNeighbourAlgorithms.Algorithm = NearestNeighbourAlgorithms.Auto,
                        runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = s"nearestNeighbours_${neighboursNumber}_${algorithm.name}"
    override val runs: Int = runsQuantity

    override def function(inputFilename: String, split: Option[Int] = None): Errors = {
      runRegressionNearestNeighbourWith(neighboursNumber, algorithm.name)(inputFilename, split)
      Errors(rmse, absoluteErrorDivMean, relativeError)
    }
  }

  def svm(kernel: SvmKernels.Kernel = SvmKernels.Rbf,
          c: Double = 1.0,
          epsilon: Double = 0.1,
          runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = s"svm_${kernel.name}_c${c}_eps$epsilon"
    override val runs: Int = runsQuantity

    override def function(inputFilename: String, split: Option[Int] = None): Errors = {
      runRegressionSvmWith(kernel.name, c, epsilon)(inputFilename, split)
      Errors(rmse, absoluteErrorDivMean, relativeError)
    }
  }

  def multilayerPerceptron(layers: Int, layerSize: Int, runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = s"mlp_${layers}_$layerSize"
    override val runs: Int = runsQuantity

    override def function(inputFilename: String, split: Option[Int] = None): Errors = {
      runRegressionMlpWith(layers, layerSize)(inputFilename, split)
      Errors(rmse, absoluteErrorDivMean, relativeError)
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
