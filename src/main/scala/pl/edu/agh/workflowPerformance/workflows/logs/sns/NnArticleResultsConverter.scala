package pl.edu.agh.workflowPerformance.workflows.logs.sns

import pl.edu.agh.workflowPerformance.Settings

import scala.io.Source
import scala.reflect.io.File

/**
  * @author lewap
  * @since 22.04.17
  */
object NnArticleResultsConverter extends Settings {

  val pathPrefix = resourcesData("snsWorkflows/nnResults")
  val tasksPath = pathPrefix + "/tasks"
  val trainingData = pathPrefix + "/workflow_performance_compact_training_25.csv"
  val testingData = pathPrefix + "/workflow_performance_compact_testing_25.csv"

  val tasks = Map(
    "namd_ID0000002" -> File(tasksPath + "/namd2"),
    "namd_ID0000003" -> File(tasksPath + "/namd3"),
    "sassena_ID0000005" -> File(tasksPath + "/sassena5"),
    "sassena_ID0000006" -> File(tasksPath + "/sassena6")
  )

  def main(args: Array[String]): Unit = {
    val training = Source.fromFile(trainingData).getLines().toList.tail
    val testing = Source.fromFile(testingData).getLines().toList.tail
    val data = training ++ testing

    val split = data.map(_.split(',').toList)
    val pointInTime = split.column(1).map(_.toDouble)
    val atoms = split.column(2).map(Mapper.from)
    val cores = split.column(3).map(Mapper.from)
    val timesteps = split.column(4).map(Mapper.from)
    val outputFrequency = split.column(5).map(Mapper.from)
    val taskId = split.column(6).map(_.replaceAll("\"", ""))
    val stime = split.column(7).map(_.toDouble)
    val utime = split.column(8).map(_.toDouble)
    val writeBytes = split.column(10).map(_.toDouble)
    val readBytes = split.column(11).map(_.toDouble)

    val normalizedPointInTime = normalize(pointInTime)
    val normalizedStime = normalize(stime)
    val normalizedUtime = normalize(utime)
    val normalizedWriteBytes = normalize(writeBytes)
    val normalizedReadBytes = normalize(readBytes)

    val allData = (taskId zip atoms zip cores zip timesteps zip outputFrequency zip normalizedStime zip normalizedUtime
      zip normalizedWriteBytes zip normalizedReadBytes zip normalizedPointInTime) map {
      case (((((((((a, b), c), d), e), f), g), h), i), j) => Row(a, List(b, c, d, e, f, g, h, i, j))
    }
    allData foreach { row =>
      tasks.get(row.task) foreach { file =>
        file.appendAll(row.data.mkString(",") + "\n")
      }
    }

  }

  def normalize(column: List[Double]): List[Double] =
    column.map(_ / column.max)

  implicit class ListWrapper(list: List[List[String]]) {
    def column(n: Int): List[String] = {
      list.map(_.apply(n))
    }
  }

  case class Row(task: String, data: List[Double])

}

object Mapper {
  val values = Map[String, Double]("\"L\"" -> 0.0, "\"M\"" -> 0.5, "\"H\"" -> 1.0)

  def from(string: String): Double = values(string)
}