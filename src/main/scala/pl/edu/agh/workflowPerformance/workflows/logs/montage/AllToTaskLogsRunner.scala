package pl.edu.agh.workflowPerformance.workflows.logs.montage

import com.typesafe.scalalogging.StrictLogging
import pl.edu.agh.workflowPerformance.Settings

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.reflect.io.File

/**
  * @author lewap
  * @since 07.01.17
  */
object AllToTaskLogsRunner extends Settings with StrictLogging {

  private val montageWorkflowsDataPath = resourcesData("montageWorkflows")
  private val allLogsPath = montageWorkflowsDataPath + "/all.log"
  private val properLogsLineLength = 19

  val taskLogsDirectory = montageWorkflowsDataPath + "/tasksLogs"

  def main(args: Array[String]): Unit = {
    splitLogsByTasks()
  }

  def splitLogsByTasks(): Unit = {
    val linesIterator = Source.fromFile(allLogsPath).getLines()
    logger.debug("Read lines from {}", allLogsPath)

    val splitLines = linesIterator.foldLeft(ListBuffer.empty[Array[String]]) { (list, line) =>
      val split = line.trim.split("\\s+")
      if (split.length == properLogsLineLength)
        list += split
      else
        list
    }.toList
    logger.debug("Split lines, total {}", splitLines.size)

    val tasks = splitLines.view.map(taskNameFrom).toSet
    logger.debug("Tasks extracted: {}", tasks.mkString(", "))

    val files = filesFrom(tasks)
    splitLines foreach { line =>
      val task = taskNameFrom(line)
      val file = files(task)
      file.appendAll(writableLineFrom(line))
    }
    logger.debug("Lines written to task files")
  }

  private def taskNameFrom(allLogsLine: Array[String]): String =
    allLogsLine(5)

  private def filesFrom(tasks: Set[String]): Map[String, File] = {
    tasks.foldLeft(Map.empty[String, File]) { (map, task) =>
      val file = File(taskLogsDirectory + "/" + task)
      file.truncate()
      map + (task -> file)
    }
  }

  private def writableLineFrom(splitLine: Array[String]): String = {
    val notNullValues = splitLine.slice(0, 7) ++ splitLine.slice(9, 12) ++ splitLine.slice(14, 16) :+ splitLine(18)
    notNullValues.toList.mkString("", " ", "\n")
  }

}
