package pl.edu.agh.workflowPerformance.workflows.logs.regression

import com.typesafe.scalalogging.StrictLogging
import pl.edu.agh.workflowPerformance.Settings
import pl.edu.agh.workflowPerformance.utils.{CsvWriter, FileUtils, PlotUtils}
import pl.edu.agh.workflowPerformance.workflows.{ConverterError, Error, RegressionError, TaskError}

import scala.io.Source
import scala.language.postfixOps

/**
  * @author lewap
  * @since 16.02.17
  */
trait RegressionRunnerUtils[T <: AbstractRow] extends ErrorPersistence[T] with FileUtils with PlotUtils with CsvWriter
  with StrictLogging with Settings {

  def parseRowString(row: String): T

  val convertersRegression: Map[AbstractFeatureConverter[T], List[Regression]]
  val tasks: List[String]
  val inputDataDir: String
  val headerInInputFiles: Boolean
  val outputDir: String

  lazy val resultDir = outputDir + "/" + currentDateStringUnderscores()
  lazy val resultDirCsv = resultDir + "/csv"
  lazy val resultDirFormatted = resultDir + "/formatted"
  lazy val plotsDir = resultDir + "/plots"
  val inputFilename = tmpFile("taskLogsInput.csv")

  def runAllTasks(): Unit = {
    logger.debug("Tasks: {}", tasks.mkString(", "))
    val taskErrors: List[TaskError] = tasks.map(runSingleTask)

    persistErrorsCsv(taskErrors)
    persistErrorsFormatted(taskErrors)
  }

  private def runSingleTask(task: String): TaskError = {
    logger.info(s"Running task $task")
    val path = inputDataDir + "/" + task
    val linesWithHeader = Source.fromFile(path).getLines.toList
    val lines = if (headerInInputFiles) {
      logger.debug(s"Omitting header ${linesWithHeader.head.substring(0, 100)}...")
      linesWithHeader.tail
    } else linesWithHeader

    val converterErrors = convertersRegression map { case (converter, regressions) =>
      runSingleConverter(task, converter, regressions, lines)
    } toList

    TaskError(task, converterErrors)
  }

  private def runSingleConverter(task: String,
                                 converter: AbstractFeatureConverter[T],
                                 regressions: List[Regression],
                                 lines: List[String]): ConverterError = {
    logger.info(s"Running converter ${converter.name}")
    val csvWritable = lines map { line =>
      converter.convertWithTime(parseRowString(line))
    }
    logger.debug("Lines converted to rows")
    writeAsCsvFile(csvWritable, inputFilename)
    logger.debug(s"Rows written to file $inputFilename")

    val regressionErrors = regressions.map(runSingleRegressionFunction(task, converter, _))

    ConverterError(converter.name, converter.description, regressionErrors)
  }

  private def runSingleRegressionFunction(task: String,
                                          converter: AbstractFeatureConverter[T],
                                          regression: Regression): RegressionError = {
    logger.info(s"Running regression ${regression.name}")
    val errors = 1 to regression.runs map { run =>
      logger.debug(s"Run #$run")
      val error = regression.function(inputFilename)
      logger.debug(s"Error #$run = $error")
      if (run == 1) {
        makeComparisonPlot(
          title = s"${task}_${converter.name}_${regression.name}_comparison",
          outputFile = s"$plotsDir/$task/${regression.name}/${converter.name}.png"
        )
      }
      error
    }
    val rmse = errors.map(_.rmse).sum / regression.runs
    val absDivMean = errors.map(_.absoluteDivMean).sum / regression.runs
    val relativeError = if (errors.exists(_.relative == -1)) {
      -1.0
    } else errors.map(_.relative).sum / regression.runs
    logger.debug(s"Errors: rmse: $rmse, abs div mean: $absDivMean, relative: $relativeError, runs: ${regression.runs}")

    RegressionError(regression.name, Error(rmse, absDivMean, relativeError, regression.runs))
  }

}
