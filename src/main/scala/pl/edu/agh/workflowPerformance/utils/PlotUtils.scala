package pl.edu.agh.workflowPerformance.utils

import com.typesafe.scalalogging.StrictLogging

import scala.language.postfixOps
import scala.reflect.io.File
import scala.sys.process._

/**
  * @author lewap
  * @since 28.02.17
  */
trait PlotUtils extends StrictLogging {

  import PlotUtils._

  def makeComparisonPlot(title: String, outputFile: String, comparisonFile: String = "tmp/comparison.csv"): Unit = {
    logger.debug(s"Making plot $title")
    File(outputFile).parent.createDirectory()
    invokePythonComparisonPlot(title, outputFile, comparisonFile)
  }

}

object PlotUtils {

  private val comparison_plot_file_maker = "src/main/python3/comparison_plot.py"

  def invokePythonComparisonPlot(title: String, outputPath: String, comparisonFile: String): Unit = {
    s"python3 $comparison_plot_file_maker -t $title -c $comparisonFile -o $outputPath" !
  }

}