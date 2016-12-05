package pl.edu.agh.workflowPerformance.kfigielaGithub

import com.typesafe.scalalogging.StrictLogging
import pl.edu.agh.workflowPerformance.Settings
import pl.edu.agh.workflowPerformance.kfigielaGithub.Entities.ExecTime
import pl.edu.agh.workflowPerformance.utils.{Combinatorial, CsvWriter}

import scala.io.Source
import scala.language.postfixOps
import scala.reflect.io.File
import scala.sys.process._

/**
  * @author lewap
  * @since 17.11.16
  */
object RunnerNormalEquationsOctave extends Settings with Combinatorial with CsvWriter with StrictLogging {

  val featuresConverter = new FeaturesConverter()
  val inputFilename = Settings.dataFile

  val instancesResults = resultFile("instancesResults")
  val taskResults = resultFile("tasksResults")

  def main(args: Array[String]): Unit = {
    runAllInstancesSubsets(instancesResults)
    runAllTasksSubsets(taskResults)
  }

  def runAllInstancesSubsets(resultFile: String = instancesResults): Unit = {
    val instanceNameExtractor = (execTime: ExecTime) => execTime.instance.name
    runAllSubsets(featuresConverter.instances.keySet, minimumSize = 12, instanceNameExtractor, resultFile)
  }

  def runAllTasksSubsets(resultFile: String = taskResults): Unit = {
    val taskNameExtractor = (execTime: ExecTime) => execTime.task.name
    runAllSubsets(featuresConverter.tasks.keySet, minimumSize = 2, taskNameExtractor, resultFile)
  }

  private def runAllSubsets(set: Set[String],
                            minimumSize: Int,
                            extractName: ExecTime => String,
                            resultFile: String): Unit = {

    val subsets = subsetsFromList[String](set.toList).filter(_.size >= minimumSize)
    logger.info("Starting computation for {} subsets", subsets.size)

    val iterator = subsets.indices.iterator
    val results = subsets map { subset =>
      logger.info("Computing subset #{}", iterator.next())
      val filteredExecTimes = featuresConverter.execTimes.filter(
        row => subset.toSet.contains(extractName(row))
      )
      writeAsCsvFile(
        featuresConverter.fullDataWithTaskDummyCoding(filteredExecTimes),
        inputFilename
      )
      s"octave src/main/octave/linearRegression/workflowsNormalEquations.m  $inputFilename" !
      val rmse = Source.fromFile(Settings.rootMeanSquareErrorPath).getLines().toList.head.toDouble
      logger.info("rmse = {}, subset = {}", "%e".format(rmse), subset.mkString(" "))
      subset -> rmse
    }

    val sortedResults = results sortBy { case (list, error) => error }
    val printableResults = sortedResults map { case (list, error) => s"$error, ${list.size}, ${list.mkString(" ")}" }
    File(resultFile).writeAll(printableResults.mkString("\n"))

  }

}
