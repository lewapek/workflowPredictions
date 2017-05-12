package pl.edu.agh.workflowPerformance.workflows.logs.regression

import pl.edu.agh.workflowPerformance.utils.PlotUtils
import pl.edu.agh.workflowPerformance.workflows.{ConverterError, Errors, RegressionError, TaskError}

import scala.collection.mutable
import scala.reflect.io.File

/**
  * @author lewap
  * @since 26.02.17
  */
trait ErrorPersistence[T <: AbstractRow] extends PlotUtils {
  this: RegressionRunnerUtils[T] =>

  def persistErrorsFormatted(taskErrors: List[TaskError]): Unit = {

    def appendHeader(file: File, converterError: ConverterError): Unit = {
      file.appendAll(converterError.converterName + "\n")
      file.appendAll(converterError.converterDescription + "\n")
      file.appendAll(f"${"task"}%20s${"rmse"}%15s${"mae"}%15s${"absDivMean"}%15s${"relative"}%15s${"runs"}%15s\n")
    }

    def appendLine(file: File, regressionError: RegressionError, taskName: String): Unit = {
      val rmse = regressionError.error.rmse
      val mae = regressionError.error.mae
      val absDivMean = regressionError.error.absoluteDivMean
      val relativeError = regressionError.error.relative
      val runs = regressionError.error.runs
      file.appendAll(f"$taskName%20s$rmse%15.4f$mae%15.4f$absDivMean%15.4f$relativeError%15.4f$runs%15d\n")
    }

    persistErrors(taskErrors, resultDirFormatted, appendHeader, appendLine)
  }

  def persistErrorsCsv(taskErrors: List[TaskError]): Unit = {

    def appendHeader(file: File, converterError: ConverterError): Unit = {
      file.appendAll("task,rmse,mae,absDivMean,relative,runs\n")
    }

    def appendLine(file: File, regressionError: RegressionError, taskName: String): Unit = {
      val rmse = regressionError.error.rmse
      val mae = regressionError.error.mae
      val absDivMean = regressionError.error.absoluteDivMean
      val relativeError = regressionError.error.relative
      val runs = regressionError.error.runs
      file.appendAll(s"$taskName,$rmse,$mae,$absDivMean,$relativeError,$runs\n")
    }

    persistErrors(taskErrors, resultDirCsv, appendHeader, appendLine, fileExtension = Some("csv"))
  }

  private def persistErrors(taskErrors: List[TaskError],
                            resultDir: String,
                            appendHeader: (File, ConverterError) => Unit,
                            appendLine: (File, RegressionError, String) => Unit,
                            fileExtension: Option[String] = None): Unit = {
    File(resultDir).createDirectory(force = true)
    File(resultDirByTask).createDirectory(force = true)

    val files: Map[(String, String), File] = taskErrors.headOption map { taskError =>
      taskError.converterErrors.foldLeft(Map.empty[(String, String), File]) { (map, converterError) =>
        val files = converterError.regressionErrors.foldLeft(Map.empty[(String, String), File]) { (map, regressionError) =>
          val path = resultDir + "/" + regressionError.regressionName + "_" + converterError.converterName
          val fullPath = fileExtension.map(path + "." + _).getOrElse(path)
          val file = File(fullPath)
          file.truncate()
          appendHeader(file, converterError)
          map + ((converterError.converterName -> regressionError.regressionName) -> file)
        }
        map ++ files
      }
    } getOrElse Map.empty[(String, String), File]
    logger.debug(s"Files to write results into: $files")

    taskErrors foreach { taskError =>
      val task = taskError.taskName
      taskError.converterErrors foreach { converterError =>
        converterError.regressionErrors foreach { regressionError =>
          val file = files(converterError.converterName -> regressionError.regressionName)
          appendLine(file, regressionError, task)
        }
      }
    }

    compareByTasks(taskErrors)
  }

  private def compareByTasks(taskErrors: List[TaskError]): Unit = {
    taskErrors foreach { taskError =>
      val outputFilePrefix = resultDirByTask + "/" + taskError.taskName
      val csvFile = File(outputFilePrefix + ".csv")
      val compactErrors: Map[(String, String), Errors] = extractTaskErrorsFrom(taskError)

      val header = "converter,regression,rmse,absoluteDivMean,relative,runs\n"
      val printableErrors = compactErrors map { case ((converter, regression), errors) =>
        converter + "," + regression + "," + errors.rmse + "," + errors.absoluteDivMean + "," + errors.relative + "," +
          errors.runs
      } mkString "\n"

      csvFile.writeAll(header + printableErrors)
      makeErrorComparisonPlot(
        titlePrefix = taskError.taskName,
        comparisonInputFile = csvFile.path,
        outputFilePrefix = outputFilePrefix
      )
    }
  }

  private def extractTaskErrorsFrom(taskError: TaskError): Map[(String, String), Errors] = {
    val result = mutable.Map.empty[(String, String), Errors]
    taskError.converterErrors foreach { converterError =>
      converterError.regressionErrors foreach { regressionError =>
        result(converterError.converterName -> regressionError.regressionName) = regressionError.error
      }
    }
    result.toMap
  }

}
