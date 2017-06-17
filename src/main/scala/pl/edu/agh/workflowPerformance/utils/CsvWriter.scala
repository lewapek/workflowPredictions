package pl.edu.agh.workflowPerformance.utils

import scala.reflect.io.File

/**
  * @author lewap
  * @since 05.12.16
  */
trait CsvWriter {

  def writeAsCsvFile(content: List[List[AnyVal]], filename: String): Unit = {
    val file = File(filename)
    file.parent.createDirectory()
    file.truncate()
    content foreach { row =>
      file.appendAll(row.mkString(",") + "\n")
    }
  }

}
