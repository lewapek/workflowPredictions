package pl.edu.agh.workflowPerformance.workflows.logs.regression

import pl.edu.agh.workflowPerformance.workflows.{ConverterError, RegressionError, TaskError}

import scala.reflect.io.File

/**
  * @author lewap
  * @since 26.02.17
  */
trait ErrorPersistence[T <: AbstractRow] {
  this: RegressionRunnerUtils[T] =>

  def persistErrorsFormatted(taskErrors: List[TaskError]): Unit = {

    def appendHeader(file: File, converterError: ConverterError): Unit = {
      file.appendAll(converterError.converterName + "\n")
      file.appendAll(converterError.converterDescription + "\n")
      file.appendAll(f"${"task"}%20s${"rmse"}%15s${"absDivMean"}%15s${"relative"}%15s${"runs"}%15s\n")
    }

    def appendLine(file: File, regressionError: RegressionError, taskName: String): Unit = {
      val rmse = regressionError.error.rmse
      val absDivMean = regressionError.error.absoluteDivMean
      val relativeError = regressionError.error.relative
      val runs = regressionError.error.runs
      file.appendAll(f"$taskName%20s$rmse%15.2f$absDivMean%15.2f$relativeError%15.2f$runs%15d\n")
    }

    persistErrors(taskErrors, resultDirFormatted, appendHeader, appendLine)
  }

  def persistErrorsCsv(taskErrors: List[TaskError]): Unit = {

    def appendHeader(file: File, converterError: ConverterError): Unit = {
      file.appendAll("task,rmse,absDivMean,relative,runs\n")
    }

    def appendLine(file: File, regressionError: RegressionError, taskName: String): Unit = {
      val rmse = regressionError.error.rmse
      val absDivMean = regressionError.error.absoluteDivMean
      val relativeError = regressionError.error.relative
      val runs = regressionError.error.runs
      file.appendAll(s"$taskName,$rmse,$absDivMean,$relativeError,$runs\n")
    }

    persistErrors(taskErrors, resultDirCsv, appendHeader, appendLine, fileExtension = Some("csv"))
  }

  private def persistErrors(taskErrors: List[TaskError],
                            resultDir: String,
                            appendHeader: (File, ConverterError) => Unit,
                            appendLine: (File, RegressionError, String) => Unit,
                            fileExtension: Option[String] = None): Unit = {
    File(resultDir).createDirectory(force = true)

    val files = taskErrors.headOption map { taskError =>
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
  }
}