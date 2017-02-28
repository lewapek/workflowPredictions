package pl.edu.agh.workflowPerformance.workflows.logsSubsets

import pl.edu.agh.workflowPerformance.workflows.montage8Yaml.Entities.Performance
import pl.edu.agh.workflowPerformance.workflows.montage8Yaml.MontageYamlParser

/**
  * @author lewap
  * @since 04.12.16
  */
class FeaturesConverter extends MontageYamlParser {

  def fullData(execTimes: List[Performance] = execTimes): List[List[AnyVal]] = {
    toFeaturesFormatWithPerformance(execTimes) { row =>
      row.instance.asFeatureVector ::: row.task.asFeatureVector
    }
  }

  def withoutInstancePrice(execTimes: List[Performance] = execTimes): List[List[AnyVal]] = {
    toFeaturesFormatWithPerformance(execTimes) { row =>
      row.instance.cores :: row.instance.memoryGiB :: row.task.asFeatureVector
    }
  }

  def fullDataWithTaskDummyCoding(execTimes: List[Performance] = execTimes): List[List[AnyVal]] = {
    toFeaturesFormatWithPerformance(execTimes) { row =>
        row.task.taskCount :: row.instance.asFeatureVector ::: row.task.toDummyCoding(tasks.size - 1)
    }
  }

  def withoutInstancePriceWithTaskDummyCoding(execTimes: List[Performance] = execTimes): List[List[AnyVal]] = {
    toFeaturesFormatWithPerformance(execTimes) { row =>
      row.instance.cores :: row.instance.memoryGiB :: row.task.toDummyCoding(tasks.size - 1)
    }
  }

  private def toFeaturesFormatWithPerformance(execTimes: List[Performance])
                              (mapToFeatures: Performance => List[AnyVal]): List[List[AnyVal]] = {
    execTimes map { row =>
      row.performance :: mapToFeatures(row)
    }
  }

}
