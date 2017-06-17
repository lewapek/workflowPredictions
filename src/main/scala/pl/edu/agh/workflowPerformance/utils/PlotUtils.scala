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

  def makeComparisonPlot(title: String,
                         outputFileNoExtension: String,
                         newComparisonFile: String,
                         xlabel: String,
                         ylabel: String,
                         indexedMode: Boolean = false,
                         comparisonFile: String = "tmp/comparison.csv",
                         comparisonFileIndexed: String = "tmp/comparisonIndexed.csv"): Unit = {
    File(outputFileNoExtension).parent.createDirectory()
    val fileToCopy = if (indexedMode) comparisonFileIndexed else comparisonFile
    Files.copy(Paths.get(fileToCopy), Paths.get(newComparisonFile))

    Future {
      val errors = calculateErrorsFrom(newComparisonFile)
      File(outputFileNoExtension + "_errors.csv").writeAll(
        errors.rmse + "," + errors.mae + "," + errors.absoluteDivMean + "," + errors.relative + "\n"
      )

      if (indexedMode) {
        invokePythonComparisonPlotWithIndexingMode(title, xlabel, ylabel, outputFileNoExtension, newComparisonFile)
      } else {
        invokePythonComparisonPlot(title, xlabel, ylabel, outputFileNoExtension, newComparisonFile)
      }
    }
  }

  def makeErrorComparisonPlot(titlePrefix: String,
                              comparisonInputFile: String,
                              outputFilePrefix: String,
                              includeConverterNames: Boolean,
                              topN: Option[Int]): Unit = {
    logger.debug(s"Making error comparison plot with title prefix: $titlePrefix")
    invokePythonErrorComparisonPlot(
      title = titlePrefix,
      outputPath = outputFilePrefix,
      comparisonFile = comparisonInputFile,
      includeConverterNames = includeConverterNames,
      topN = topN
    )
  }

}

object PlotUtils extends StrictLogging {

  private val comparisonPlot = "src/main/python3/comparison_plot.py"
  private val errorComparisonPlot = "src/main/python3/errors_comparison_plot.py"

  def invokePythonComparisonPlot(title: String, xlabel: String, ylabel: String,
                                 outputPath: String, comparisonFile: String): Unit = {
    val command = Seq("python3", comparisonPlot, "-t", title, "-x", xlabel, "-y", ylabel, "-c", comparisonFile, "-o", outputPath)
    logger.debug(s"Making plot with command: $command")
    command !
  }

  def invokePythonComparisonPlotWithIndexingMode(title: String, xlabel: String, ylabel: String,
                                                 outputPath: String, comparisonFile: String): Unit = {
    val command = Seq("python3", comparisonPlot, "-t", title, "-x", xlabel, "-y", ylabel, "-c", comparisonFile, "-o", outputPath, "-i", "True")
    logger.debug(s"Making plot in indexing mode with command: $command")
    command !
  }

  def invokePythonErrorComparisonPlot(title: String,
                                      outputPath: String,
                                      comparisonFile: String,
                                      includeConverterNames: Boolean,
                                      topN: Option[Int]): Unit = {
    val topArgument = topN.map(top => s" --top $top ").getOrElse("")
    val includeConverterNamesArgument = if (includeConverterNames) " -a " else ""

    s"python3 $errorComparisonPlot -t $title -c $comparisonFile -o $outputPath $includeConverterNamesArgument $topArgument" !
  }

}