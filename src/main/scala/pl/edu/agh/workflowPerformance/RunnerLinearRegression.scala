package pl.edu.agh.workflowPerformance

import java.io.File
import java.text.DecimalFormat

import org.apache.commons.io.FileUtils
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.sql.SparkSession
import pl.edu.agh.workflowPerformance.kfigielaGithub.Parser


/**
  * @author lewap
  * @since 07.11.16
  */
case class Row(execTime: Double,
               taskCount: Int, dataSizeInput: Double, dataSizeOutput: Double,
               price: Double, cores: Int)

object RunnerLinearRegression extends Parser {

  val executionTimes = execTimes
  tasks

  val taskCount = tasks map { case (_, v) =>
    v.taskCount.toDouble
  }
  val taskCountRange = taskCount.max - taskCount.min
  val dataInCount = tasks map { case (_, v) =>
    v.dataSizeInput
  }
  val dataInRange = dataInCount.max - dataInCount.min
  val dataOutCount = tasks map { case (_, v) =>
    v.dataSizeOutput
  }
  val dataOutRange = dataOutCount.max - dataOutCount.min

  val coresCount = instances map { case (_, v) =>
    v.cores.toDouble
  }
  val coresRange = coresCount.max - coresCount.min
  val priceCount = instances map { case (_, v) =>
    v.price
  }
  val priceRange = priceCount.max - priceCount.min

  def main(args: Array[String]): Unit = {

    val spark: SparkSession = SparkSession
      .builder
      .appName("montageLinearRegression").master("local")
      .getOrCreate()

    val dataFile = prepareData()
    val training = spark.read.format("libsvm").load("montageData2")

    val lr = new LinearRegression()
//      .setMaxIter(100)
//      .setRegParam(0.3)
//      .setElasticNetParam(0.8)

    val lrModel = lr.fit(training)

    println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")
    println(s"coef size = ${lrModel.coefficients.size}")

    val trainingSummary = lrModel.summary
    println(s"numIterations: ${trainingSummary.totalIterations}")
    println(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]")
    trainingSummary.residuals.show()
    println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
    println(s"r2: ${trainingSummary.r2}")

    val c = lrModel.coefficients
    val formatter = new DecimalFormat("#.##")
    executionTimes foreach { sample =>
      val predicted = lrModel.intercept + sample.instance.cores / coresRange * c(0) + sample.instance.price / priceRange * c(1) +
        sample.task.taskCount / taskCountRange * c(2) + sample.task.dataSizeInput / dataInRange * c(3) + sample.task.dataSizeOutput / dataOutRange * c(4)

      println(s"real = ${formatter.format(sample.time)}, predicted = ${formatter.format(predicted)}")
    }

    spark.stop()
  }

  private def prepareData(): String = {
    val filename = "montageData2"

    val stringBuilder = new StringBuilder()
    executionTimes foreach { time =>
      stringBuilder.append(time.time)
      val features = Seq(
        time.instance.cores / coresRange, time.instance.price / priceRange,
        time.task.taskCount / taskCountRange, time.task.dataSizeInput / dataInRange, time.task.dataSizeOutput / dataOutRange
      )
      val iter = (1 to 6).iterator
      features.foreach(
        feature => stringBuilder.append(s",$feature")
      )
      stringBuilder.append("\n")
    }

    FileUtils.writeStringToFile(new File(filename), stringBuilder.toString())

    filename
  }
}
