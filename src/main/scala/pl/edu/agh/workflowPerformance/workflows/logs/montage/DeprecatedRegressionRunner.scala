package pl.edu.agh.workflowPerformance.workflows.logs.montage

import com.typesafe.scalalogging.StrictLogging
import pl.edu.agh.workflowPerformance.Settings
import pl.edu.agh.workflowPerformance.utils.Octave.RegressionFunction
import pl.edu.agh.workflowPerformance.utils.{CsvWriter, FileUtils, Octave}
import pl.edu.agh.workflowPerformance.workflows.logs.AbstractFeatureConverter
import pl.edu.agh.workflowPerformance.workflows.logs.montage.featureConverters._
import pl.edu.agh.workflowPerformance.workflows.logs.montage.structure.{MontageRow, MontageRowParser}
import pl.edu.agh.workflowPerformance.workflows.montage8Yaml.MontageYamlParser

import scala.io.Source
import scala.language.postfixOps
import scala.reflect.io.File

/**
  * @author lewap
  * @since 08.01.17
  */
object DeprecatedRegressionRunner extends Settings with CsvWriter with FileUtils with MontageRowParser with StrictLogging {

  object Infrastructure extends MontageYamlParser

  private val montageWorkflowsDataPath = resourcesData("montageWorkflows")
  val taskLogsDirectory = montageWorkflowsDataPath + "/tasksLogs"
  val inputFilename = tmpFile("taskLogsInput.csv")
  val resultsDirectory = "results/tasksLogsRmse"
  val subdirectory = currentDateStringUnderscores()

  def main(args: Array[String]): Unit = {
    val regressionFunctions: List[(String, RegressionFunction)] = List(
      "gradientDescent" -> Octave.runLinearRegressionGradientDescentWith,
      "normalEquations" -> Octave.runNormalEquationsWith
    )
    val converters = List(
      ConverterLinearFull,
      ConverterLinearNoCores,
      ConverterLinearNoCoresAndMem,
      ConverterLinearNoOutputSize,
      ConverterLinearNoDataSizes,
      ConverterLinearNoInstance,
      ConverterLinearNoInstanceMontageSquare,
      ConverterLinearNoNetwork,
      ConverterCoresInversion,
      ConverterCoresAndMemInversion
    )

    runAllTasksWith(regressionFunctions, converters)
  }

  private def runAllTasksWith(regressionFunctions: List[(String, RegressionFunction)],
                              converters: List[AbstractFeatureConverter[MontageRow]]): Unit = {
    val tasks = listFilesFrom(taskLogsDirectory)
    logger.debug("Tasks: {}", tasks.mkString(", "))

    converters foreach { converter =>
      logger.info(s"Running converter: ${converter.name}")

      regressionFunctions foreach { case (functionName, regressionFunction) =>
        logger.info(s"Running regression function: $functionName")
        runTasks(tasks, functionName -> regressionFunction, converter)
      }

    }
  }

  private def runTasks(tasks: List[String],
                       regression: (String, RegressionFunction),
                       converter: AbstractFeatureConverter[MontageRow]): Unit = {
    val (regressionName, regressionFunction) = regression
    val errors = tasks.foldLeft(Map.empty[String, (Double, Double)]) { (map, task) =>
      val rmseAndRelativeErrors = runSingleTask(task, regressionFunction, converter)
      map + (task -> rmseAndRelativeErrors)
    }
    logger.debug("Got rmse and relative errors: {}", errors.mkString(", "))

    val writableErrors = errors.toList.sorted map { case (key, (rmse, relativeError)) =>
      f"$key%20s$rmse%15.2f$relativeError%15.2f"
    }

    val directory = resultsDirectory + "/" + subdirectory
    val outputPath = directory + "/" + regressionName + "_" + converter.name
    File(directory).createDirectory(force = true)
    val outputFile = File(outputPath)
    outputFile.truncate()
    outputFile.appendAll(converter.name + "\n")
    outputFile.appendAll(converter.description + "\n")
    outputFile.appendAll(f"${"task"}%20s${"rmse"}%15s${"relative"}%15s\n")
    outputFile.appendAll(writableErrors.mkString("\n"))
  }

  private def runSingleTask(name: String,
                            regression: RegressionFunction = Octave.runLinearRegressionGradientDescentWith,
                            converter: AbstractFeatureConverter[MontageRow]): (Double, Double) = {
    logger.debug("Running task: {}", name)
    val path = AllToTaskLogsRunner.taskLogsDirectory + "/" + name
    val linesIterator = Source.fromFile(path).getLines
    val csvWritable = linesIterator map { line =>
      converter.convertWithTime(parseMontageRow(line))
    } toList

    writeAsCsvFile(csvWritable, inputFilename)
    val rmse = regression(inputFilename)
    val relativeError = Octave.relativeError

    logger.debug(s"rmse: $rmse, relative error = $relativeError")

    (rmse, relativeError)
  }

}
