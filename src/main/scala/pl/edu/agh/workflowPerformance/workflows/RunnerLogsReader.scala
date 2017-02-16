package pl.edu.agh.workflowPerformance.workflows

import com.typesafe.scalalogging.StrictLogging
import pl.edu.agh.workflowPerformance.Settings

import scala.collection.mutable
import scala.io.Source
import scala.reflect.io.{Directory, File}
import scala.util.{Failure, Success, Try}

/**
  * @author lewap
  * @since 18.12.16
  */
object RunnerLogsReader extends Settings with StrictLogging {

  case class MontageInstance(instanceName: String, montage: Double) {
    def filePrefix: String = instanceName.replace('-', '.') + '.' + montage.formatted("%1.1f")

    def toFilename: String = filePrefix + ".log"
  }

  case class ContentLine(taskName: String, numbers: List[AnyVal], hash1: String, hash2: String)

  case class TimeCounter(sum: Double, total: Int, numbers: List[Double]) {
    def avg(): Double = sum / total
  }

  val logsPath = resourcesData("amazonWorkflowsKfigiela/logs")

  def main(args: Array[String]): Unit = {
    val montageInstances = listFilesFrom(logsPath).map(montageInstanceFrom)
    val instances = montageInstances.foldLeft(Set.empty[String])(_ + _.instanceName)
    val montages = montageInstances.foldLeft(Set.empty[Double])(_ + _.montage)
    println(instances, montages)

    //    val contents = montageInstances.foldLeft(Map.empty[MontageInstance, List[ContentLine]]) { (map, montageInstance) =>
    //      println(montageInstance)
    //      map + (montageInstance -> readContentFrom(montageInstance.toFilename))
    //    }
    //    println(contents)

    montageInstances foreach { log =>
      logger.info("Parsing {}", log)

      val lines = Source.fromFile(logsPath + "/" + log.toFilename).getLines()

      val timeCounters = lines.foldLeft(Map.empty[String, TimeCounter]) { (map, line) =>
        val split = line.split("\\s+")
        val task = split(5)
        val time = split(6).toDouble
        val numbers = split(3).toDouble :: split(9).toDouble :: split(10).toDouble :: split(11).toDouble :: split(14).toDouble ::
          split(15).toDouble :: Nil
        map.get(task) map { previousTimeCounter =>
          val updatedSum = previousTimeCounter.sum + time
          val previousNumbers = previousTimeCounter.numbers
          val updatedNumbers = previousNumbers.zip(numbers) map { case (a, b) => a + b }
          map + (task -> previousTimeCounter.copy(sum = updatedSum, previousTimeCounter.total + 1, updatedNumbers))
        } getOrElse {
          map + (task -> TimeCounter(time, 1, numbers))
        }
      }

      val fileToSave = logsPath + "/" + log.filePrefix + ".time"
      val printable = timeCounters map { case (taskName, timeCounter) =>
        s"${taskName.formatted("%-15s")} ${timeCounter.avg().formatted("%-13.7f")}" +
          s" ${timeCounter.total.formatted("%-7d")} ${timeCounter.numbers.map(_.formatted("%-20.2f")).mkString(" ")}"
      }
      File(fileToSave).writeAll(printable.mkString("\n"))
    }

  }

  private def listFilesFrom(directory: String): List[String] = {
    Directory(directory).files.map(_.name).toList
  }

  private def montageInstanceFrom(filename: String): MontageInstance = {
    val split = filename.split('.')
    val instanceName = split.head + '-' + split(1)
    val montage: Double = s"${split(2)}.${split(3)}".toDouble
    MontageInstance(instanceName, montage)
  }

  private def readContentFrom(filename: String): List[ContentLine] = {
    val lines = Source.fromFile(logsPath + "/" + filename).getLines()
    val split = lines.map(_.split("\\S"))
    val contents = split map { line =>
      val taskName = line(5)
      val hash1 = line(2)
      val hash2 = line(4)
      val numbers = numbersFrom(line)
      ContentLine(taskName, numbers, hash1, hash2)
    }
    contents.toList
  }

  private def numbersFrom(stringArray: Array[String]): List[AnyVal] = {
    val columns = stringArray(3) :: stringArray.slice(6, stringArray.length).toList
    columns.foldRight(List.empty[AnyVal]) { (element, list) =>
      Try(element.toInt) match {
        case Success(int) =>
          int :: list
        case Failure(_) =>
          Try(element.toDouble) match {
            case Success(double) => double :: list
            case Failure(_) => list
          }
      }
    }
  }

}
