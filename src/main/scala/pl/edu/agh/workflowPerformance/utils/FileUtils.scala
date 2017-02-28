package pl.edu.agh.workflowPerformance.utils

import java.text.SimpleDateFormat
import java.util.Date

import scala.reflect.io.Directory

/**
  * @author lewap
  * @since 08.01.17
  */
trait FileUtils {

  private val dateFormatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")

  def listFilesFrom(directory: String): List[String] =
    Directory(directory).files.map(_.name).toList.sorted

  def currentDateStringUnderscores(): String =
    dateFormatter.format(new Date())

}
