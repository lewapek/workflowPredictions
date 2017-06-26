package pl.edu.agh.workflowPerformance.workflows.logs.montage

import com.typesafe.scalalogging.StrictLogging
import pl.edu.agh.workflowPerformance.Settings
import pl.edu.agh.workflowPerformance.utils.FileUtils
import pl.edu.agh.workflowPerformance.workflows.logs.Stats
import pl.edu.agh.workflowPerformance.workflows.logs.montage.structure.MontageRowParser

import scala.io.Source
import scala.reflect.io.File

/**
  * @author lewap
  * @since 26.01.17
  */
object VariancesRunner extends MontageRowParser with FileUtils with Settings with StrictLogging {

  case class InstanceMontageTime(instance: String, montage: Double, time: Double) {
    def toInstanceMontage = InstanceMontage(instance, montage)
  }

  case class InstanceMontage(instance: String, montage: Double)

  case class TotalTime(length: Long, times: List[Double])

  val outputFile = File(resultFile("montageTasks/stats/stats"))

  def main(args: Array[String]): Unit = {
    val tasks = listFilesFrom(AllToTaskLogsRunner.taskLogsDirectory).sorted
    logger.debug("Tasks: {}", tasks.mkString(", "))

    outputFile.truncate()
    outputFile.appendAll(f"${"task, w=weighted"}%20s${"w stdev"}%15s${"stdev"}%15s${"w variance"}%15s${"variance"}%15s\n")
    tasks foreach { task =>
      logger.info("Analyzing task: {}", task)
      statsFromSingleTask(task)
    }
  }

  private def statsFromSingleTask(taskName: String): Unit = {
    val task = AllToTaskLogsRunner.taskLogsDirectory + "/" + taskName
    val linesIterator = Source.fromFile(task).getLines()

    val parsed: List[InstanceMontageTime] = linesIterator.map(line => {
      val row = parseMontageRow(line)
      InstanceMontageTime(row.instance.name, row.montage, row.y)
    }).toList

    val distinctSeries = parsed.foldLeft(Map.empty[InstanceMontage, TotalTime]) { (map, instanceMontageTime) =>
      val instanceMontage = instanceMontageTime.toInstanceMontage
      map.get(instanceMontage) map { totalTime =>
        map + (instanceMontage -> TotalTime(totalTime.length + 1, instanceMontageTime.time :: totalTime.times))
      } getOrElse {
        map + (instanceMontage -> TotalTime(1, instanceMontageTime.time :: Nil))
      }
    }

    val stats = distinctSeries map { case (instanceMontage, totalTime) =>
      instanceMontage -> Stats.from(totalTime)
    }

    val totalQuantity = stats.values.map(_.quantity).sum

    val weightedStdevSum = stats.values.map(stat => stat.quantity * stat.stdev).sum
    val weightedStdevMean = weightedStdevSum / totalQuantity

    val weightedVarianceSum = stats.values.map(stat => stat.quantity * stat.variance).sum
    val weightedVarianceMean = weightedVarianceSum / totalQuantity

    val stdevSum = stats.values.map(_.stdev).sum
    val stdevMean = stdevSum / stats.size

    val varianceSum = stats.values.map(_.variance).sum
    val varianceMean = varianceSum / stats.size

    if (stats.size < stats.values.map(_.quantity).sum) {
      logger.info("Non-empty stats, size = {}", stats.size)
      outputFile.appendAll(
        f"${task.split('/').last}%20s$weightedStdevMean%15.2f$stdevMean%15.2f$weightedVarianceMean%15.2f$varianceMean%15.2f\n"
      )
    } else {
      logger.info("Empty stats")
      outputFile.appendAll(
        f"${task.split('/').last}%20s" + (f"${"-"}%15s" * 4) + "\n"
      )
    }

  }

}
