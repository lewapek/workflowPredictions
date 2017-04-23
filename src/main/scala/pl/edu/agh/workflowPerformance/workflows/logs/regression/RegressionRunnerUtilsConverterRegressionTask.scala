package pl.edu.agh.workflowPerformance.workflows.logs.regression

import com.typesafe.scalalogging.StrictLogging
import pl.edu.agh.workflowPerformance.Settings
import pl.edu.agh.workflowPerformance.utils.ExternalRegressionExecutor._
import pl.edu.agh.workflowPerformance.utils.{CsvWriter, ExternalRegressionExecutor, FileUtils}

import scala.io.Source
import scala.language.postfixOps
import scala.reflect.io.File

/**
  * @author lewap
  * @since 16.02.17
  */
trait RegressionRunnerUtilsConverterRegressionTask[T <: AbstractRow] extends FileUtils with CsvWriter with StrictLogging with Settings {

  def parseRowString(row: String): T

  val converters: List[AbstractFeatureConverter[T]]
  val tasks: List[String]
  val inputDataDir: String
  val headerInInputFiles: Boolean
  val outputDir: String

  lazy val resultDir = outputDir + "/" + currentDateStringUnderscores()
  val inputFilename = tmpFile("taskLogsInput.csv")

  def gradientDescent(filename: String): Double =
    ExternalRegressionExecutor.runLinearRegressionGradientDescentWith(filename)

  def normalEquations(filename: String): Double =
    ExternalRegressionExecutor.runNormalEquationsWith(filename)

  def decisionTree(filename: String): Double =
    ExternalRegressionExecutor.runRegressionDecisionTreeWith(treeMaxDepth = 10)(filename)

  val regressionFunctions: List[(String, RegressionFunction)] = List(
    //    "gradientDescent" -> Octave.runLinearRegressionGradientDescentWith,
    //    "normalEquations" -> Octave.runNormalEquationsWith,
    "decisionTree" -> decisionTree
  )

  def runAllTasks(): Unit =
    runAllTasksWith(regressionFunctions, converters)

  private def runAllTasksWith(regressionFunctions: List[(String, RegressionFunction)],
                              converters: List[AbstractFeatureConverter[T]]): Unit = {
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
                       converter: AbstractFeatureConverter[T]): Unit = {
    val (regressionName, regressionFunction) = regression
    val errors = tasks.foldLeft(Map.empty[String, (Double, Double)]) { (map, task) =>
      val rmseAndRelativeErrors = runSingleTask(task, regressionFunction, converter)
      map + (task -> rmseAndRelativeErrors)
    }
    logger.debug("Got rmse and relative errors: {}", errors.mkString(", "))

    val writableErrors = errors.toList.sorted map { case (key, (rmse, relativeError)) =>
      f"$key%20s$rmse%15.2f$relativeError%15.2f"
    }

    val outputPath = resultDir + "/" + regressionName + "_" + converter.name
    logger.debug(s"Writing to file $outputPath")
    File(resultDir).createDirectory(force = true)
    val outputFile = File(outputPath)
    outputFile.truncate()
    outputFile.appendAll(converter.name + "\n")
    outputFile.appendAll(converter.description + "\n")
    outputFile.appendAll(f"${"task"}%20s${"rmse"}%15s${"relative"}%15s\n")
    outputFile.appendAll(writableErrors.mkString("\n"))
  }

  private def runSingleTask(name: String,
                            regression: RegressionFunction = gradientDescent,
                            converter: AbstractFeatureConverter[T]): (Double, Double) = {
    logger.debug("Running task: {}", name)
    val path = inputDataDir + "/" + name
    val linesIterator = Source.fromFile(path).getLines
    if (headerInInputFiles) {
      val header = linesIterator.next()
      logger.debug(s"Omitting header ${header.substring(0, 100)}...")
    }
    val csvWritable = linesIterator map { line =>
      converter.convertWithTime(parseRowString(line))
    } toList

    logger.debug("Lines converted to rows")

    writeAsCsvFile(csvWritable, inputFilename)
    val rmse = regression(inputFilename)
    val relativeError = ExternalRegressionExecutor.relativeError

    logger.debug(s"rmse: $rmse, relative error = $relativeError")

    (rmse, relativeError)
  }

}
