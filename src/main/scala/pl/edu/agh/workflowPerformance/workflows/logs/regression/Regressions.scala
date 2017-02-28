package pl.edu.agh.workflowPerformance.workflows.logs.regression

import pl.edu.agh.workflowPerformance.utils.ExternalRegressionExecutor._
import pl.edu.agh.workflowPerformance.workflows.Error

/**
  * @author lewap
  * @since 26.02.17
  */
object Regressions {

  def normalEquations(runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = "normalEquations"
    override val runs: Int = 1

    override def function(inputFilename: String): Error = {
      runNormalEquationsWith(inputFilename)
      Error(rmse, absoluteErrorDivMean, relativeError)
    }
  }

  def gradientDescent(runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = "gradientDescent"
    override val runs: Int = runsQuantity

    override def function(inputFilename: String): Error = {
      runLinearRegressionGradientDescentWith(inputFilename)
      Error(rmse, absoluteErrorDivMean, relativeError)
    }
  }

  def decisionTree(maxDepth: Int = 10, runsQuantity: Int = 1): Regression = new Regression {
    override val name: String = "decisionTree"
    override val runs: Int = runsQuantity

    override def function(inputFilename: String): Error = {
      runRegressionDecisionTreeWith(treeMaxDepth = maxDepth)(inputFilename)
      Error(rmse, absoluteErrorDivMean, relativeError)
    }

  }

}
