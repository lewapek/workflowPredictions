package pl.edu.agh.workflowPerformance.workflows.logs.sns

import com.typesafe.scalalogging.StrictLogging
import pl.edu.agh.workflowPerformance.Settings
import pl.edu.agh.workflowPerformance.utils.FileUtils

import scala.io.Source
import scala.reflect.io.{Directory, File}

/**
  * @author lewap
  * @since 27.02.17
  */
object SnsLogsConversionsRunner extends Settings with FileUtils with StrictLogging {

  def main(args: Array[String]): Unit = {

    val path = resourcesData("snsWorkflows")
    val newPath = s"$path/totalTimes"
    Directory(newPath).createDirectory()

    val files = listFilesFrom(path)
    logger.debug(s"Files: $files")

    files foreach { file =>
      cutNotEndingLines(path, newPath, file)
    }

  }

  def cutNotEndingLines(oldPath: String, newPath: String, filename: String): Unit = {
    val lines = Source.fromFile(oldPath + "/" + filename).getLines().toList
    val newFile = File(newPath + "/" + filename)

    newFile.appendAll(lines.head + "\n")

    var previousRun = 0
    lines.tail foreach { line =>
      val run = line.split(',')(5).toInt
      if (run != previousRun) {
        previousRun = run
        newFile.appendAll(line + "\n")
      }
    }
  }

}
