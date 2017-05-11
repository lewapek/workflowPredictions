package pl.edu.agh.workflowPerformance.workflows.logs

import pl.edu.agh.workflowPerformance.workflows.logs.montage.VariancesRunner.TotalTime

/**
  * @author lewap
  * @since 30.01.17
  */
case class Stats(quantity: Long, min: Double, max: Double, avg: Double, variance: Double, stdev: Double)

object Stats {

  def from(totalTime: TotalTime): Stats = {
    val sum = totalTime.times.sum
    val length = totalTime.length

    val min = totalTime.times.min
    val max = totalTime.times.max
    val avg = sum / length
    val variance = totalTime.times.map(element => Math.pow(element - avg, 2)).sum / length
    val stdev = Math.sqrt(variance)

    Stats(length, min, max, avg, variance, stdev)
  }

}
