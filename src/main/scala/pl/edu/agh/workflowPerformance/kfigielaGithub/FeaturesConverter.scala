package pl.edu.agh.workflowPerformance.kfigielaGithub

import pl.edu.agh.workflowPerformance.kfigielaGithub.Entities.ExecTime

/**
  * @author lewap
  * @since 04.12.16
  */
class FeaturesConverter extends Parser {

  def fullData(execTimes: List[ExecTime] = execTimes): List[List[AnyVal]] = {
    toFeaturesFormat(execTimes) { row =>
      row.instance.asFeatureVector ::: row.task.asFeatureVector
    }
  }

  def withoutInstancePrice(execTimes: List[ExecTime] = execTimes): List[List[AnyVal]] = {
    toFeaturesFormat(execTimes) { row =>
      row.instance.cores :: row.instance.memoryGiB :: row.task.asFeatureVector
    }
  }

  def fullDataWithTaskDummyCoding(execTimes: List[ExecTime] = execTimes): List[List[AnyVal]] = {
    toFeaturesFormat(execTimes) { row =>
      row.instance.asFeatureVector ::: row.task.toDummyCoding(tasks.size - 1)
    }
  }

  def withoutInstancePriceWithTaskDummyCoding(execTimes: List[ExecTime] = execTimes): List[List[AnyVal]] = {
    toFeaturesFormat(execTimes) { row =>
      row.instance.cores :: row.instance.memoryGiB :: row.task.toDummyCoding(tasks.size - 1)
    }
  }

  private def toFeaturesFormat(execTimes: List[ExecTime])
                              (mapToFeatures: ExecTime => List[AnyVal]): List[List[AnyVal]] = {
    execTimes map { row =>
      row.time :: mapToFeatures(row)
    }
  }

}
