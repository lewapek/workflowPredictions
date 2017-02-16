package pl.edu.agh.workflowPerformance.workflows

import java.text.SimpleDateFormat
import java.util.Date

import com.typesafe.scalalogging.StrictLogging
import pl.edu.agh.workflowPerformance.Settings
import pl.edu.agh.workflowPerformance.workflows.Entities.Performance
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

  val dateFormatter = new SimpleDateFormat("_yyyy_MM_dd_hh_mm_ss")
  val featuresConverter = new FeaturesConverter()
  val inputFilename = Settings.dataFile

  val instancesResults = resultFile("instancesResults")
  val taskResults = resultFile("tasksResults")

  def main(args: Array[String]): Unit = {
    //    val tasks = Source.fromFile("results/tasksResultsExecTimeWithoutT1_2016_12_12_08_02_13").getLines().toList
    //      .head.split(',')(2).trim.split(" ").map(_.trim).toList
    //    println(tasks)
    //    val execTimes = featuresConverter.execTimesWithoutInstanceT1Micro.filter(e => tasks.contains(e.task.name))
    //    println(execTimes)
    //    writeAsCsvFile(
    //      featuresConverter.fullData(execTimes),
    //      inputFilename
    //    )
    //    s"octave src/main/octave/linearRegression/workflowsNormalEquations.m  $inputFilename" !


    //    val lines = Source.fromFile(resourcesData("amazonInstancesCloudTests/cloudTests.csv"))
    //      .getLines().toList.tail
    //      .map(_.split(",").toList)
    //      .foldLeft(Set.empty[String]) { (set, line) =>
    //        set + line.head
    //      }
    //    println(lines.size)
    //    println(featuresConverter.instances.size)
    //    println(featuresConverter.instances.keySet.map(_.replaceAll("-", "\\.")) -- lines)
    //    println(lines -- featuresConverter.instances.keySet.map(_.replaceAll("-", "\\.")))

    //    runAllInstancesSubsets(instancesResults, minimumSubsetSize = 14)

    //    val i = "T_MontagemBgModel T_MontagemAdd T_MontagemConcatFit T_MontagemShrink T_MontagemBackground T_MontagemImgtbl T_MontagemProjectPP".split(" ").toSeq
//    runSingleCase(featuresConverter.execTimesWithTasks(i: _*), featuresConverter.fullData)

//        runAllTasksSubsets(taskResults + "PerformanceWithoutCores")(
//          featuresConverter.execTimes,
//          featuresConverter.withoutInstancePrice
//        )
    println(featuresConverter.instances.keys.toList.sorted.mkString(", "))
  }

  def runAllInstancesSubsets(resultFile: String = instancesResults,
                             minimumSubsetSize: Int = 14)
                            (execTimes: List[Performance],
                             toNumbersConverter: List[Performance] => List[List[AnyVal]]): Unit = {
    val instanceNameExtractor = (execTime: Performance) => execTime.instance.name
    runAllSubsets(
      featuresConverter.instances.keySet, minimumSubsetSize, instanceNameExtractor, resultFile
    )(execTimes, toNumbersConverter)
  }

  def runAllTasksSubsets(resultFile: String = taskResults,
                         minimumSubsetSize: Int = 7)
                        (execTimes: List[Performance],
                         toNumbersConverter: List[Performance] => List[List[AnyVal]]): Unit = {
    val taskNameExtractor = (execTime: Performance) => execTime.task.name
    runAllSubsets(
      featuresConverter.tasks.keySet, minimumSubsetSize, taskNameExtractor, resultFile
    )(execTimes, toNumbersConverter)
  }

  private def runAllSubsets(set: Set[String],
                            minimumSize: Int,
                            extractName: Performance => String,
                            resultFile: String)
                           (execTimes: List[Performance],
                            toNumbersConverter: List[Performance] => List[List[AnyVal]]): Unit = {

    val computationDate = dateFormatter.format(new Date())
    println(execTimes.size, execTimes.head)

    val subsets = subsetsFromList[String](set.toList).filter(_.size >= minimumSize)
    logger.info("Starting computation for {} subsets", subsets.size)

    val iterator = subsets.indices.iterator
    val results = subsets map { subset =>
      logger.info("Computing subset #{}", iterator.next())
      val filteredExecTimes = execTimes.filter(
        row => subset.toSet.contains(extractName(row))
      )

      runSingleCase(filteredExecTimes, toNumbersConverter)

      val rmse = Source.fromFile(Settings.rootMeanSquareErrorPath).getLines().toList.head.toDouble
      logger.info("rmse = {}, subset = {}", "%e".format(rmse), subset.mkString(" "))
      subset -> rmse
    }

    val sortedResults = results sortBy { case (list, error) => error }
    val printableResults = sortedResults map { case (list, error) => s"$error, ${list.size}, ${list.mkString(" ")}" }
    File(resultFile + computationDate).writeAll(printableResults.mkString("\n"))

  }

  private def runSingleCase(rows: List[Performance],
                            toNumbersConverter: List[Performance] => List[List[AnyVal]]): Double = {
    val rowsConvertedToNumbers = toNumbersConverter(rows)
    writeAsCsvFile(rowsConvertedToNumbers, inputFilename)
    s"octave src/main/octave/linearRegression/workflowsNormalEquations.m $inputFilename" !
  }

}
