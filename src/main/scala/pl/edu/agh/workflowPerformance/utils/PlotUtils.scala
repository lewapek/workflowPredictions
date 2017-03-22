package pl.edu.agh.workflowPerformance.utils

import java.nio.file.{Files, Paths}

import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.reflect.io.File
import scala.sys.process._

/**
  * @author lewap
  * @since 28.02.17
  */
trait PlotUtils extends StrictLogging {

  import PlotUtils._

  implicit val executionContext: ExecutionContext

  def makeComparisonPlot(title: String, outputFile: String, newComparisonFile: String,
                         comparisonFile: String = "tmp/comparison.csv"): Unit = {
    File(outputFile).parent.createDirectory()
    Files.copy(Paths.get(comparisonFile), Paths.get(newComparisonFile))

    Future {
      logger.debug(s"Making plot $title")
      invokePythonComparisonPlot(title, outputFile, newComparisonFile)
    }
  }

}

object PlotUtils {

  private val comparisonPlotFileMaker = "src/main/python3/comparison_plot.py"

  def invokePythonComparisonPlot(title: String, outputPath: String, comparisonFile: String): Unit = {
    s"python3 $comparisonPlotFileMaker -t $title -c $comparisonFile -o $outputPath" !
  }

}