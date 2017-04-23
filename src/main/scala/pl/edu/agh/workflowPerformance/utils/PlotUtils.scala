package pl.edu.agh.workflowPerformance.utils

import java.nio.file.{Files, Paths}

import com.typesafe.scalalogging.StrictLogging
import pl.edu.agh.workflowPerformance.workflows.logs.regression.StatsUtils

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.reflect.io.File
import scala.sys.process._

/**
  * @author lewap
  * @since 28.02.17
  */
trait PlotUtils extends StatsUtils with StrictLogging {

  import PlotUtils._

  implicit val executionContext: ExecutionContext

  def makeComparisonPlot(title: String, outputFileNoExtension: String, newComparisonFile: String,
                         indexedMode: Boolean = false,
                         comparisonFile: String = "tmp/comparison.csv",
                         comparisonFileIndexed: String = "tmp/comparisonIndexed.csv"): Unit = {
    File(outputFileNoExtension).parent.createDirectory()
    val fileToCopy = if (indexedMode) comparisonFileIndexed else comparisonFile
    Files.copy(Paths.get(fileToCopy), Paths.get(newComparisonFile))

    Future {
      val errors = calculateErrorsFrom(newComparisonFile)
      File(outputFileNoExtension + "_errors.csv").writeAll(
        errors.rmse + "," + errors.absoluteDivMean + "," + errors.relative + "\n"
      )

      logger.debug(s"Making plot $title")
      if (indexedMode) {
        invokePythonComparisonPlotWithIndexingMode(title, outputFileNoExtension, newComparisonFile)
      } else {
        invokePythonComparisonPlot(title, outputFileNoExtension, newComparisonFile)
      }
    }
  }

}

object PlotUtils {

  private val comparisonPlotFileMaker = "src/main/python3/comparison_plot.py"

  def invokePythonComparisonPlot(title: String, outputPath: String, comparisonFile: String): Unit = {
    s"python3 $comparisonPlotFileMaker -t $title -c $comparisonFile -o $outputPath" !
  }

  def invokePythonComparisonPlotWithIndexingMode(title: String, outputPath: String, comparisonFile: String): Unit = {
    s"python3 $comparisonPlotFileMaker -t $title -c $comparisonFile -o $outputPath -i True" !
  }

}