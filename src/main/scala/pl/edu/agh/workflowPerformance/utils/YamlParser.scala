package pl.edu.agh.workflowPerformance.utils

import java.io.FileInputStream

import org.yaml.snakeyaml.Yaml

import scala.collection.JavaConverters._


/**
  * @author lewap
  * @since 08.11.16
  */
object YamlParser extends AnyConversions {

  private val yaml = new Yaml()

  def mapStringAnyFrom(yamlFilePath: String): Map[String, Any] =
    convertToScala(load(yamlFilePath)).asMapStringAny

  def listAnyFrom(yamlFilePath: String): List[Any] =
    convertToScala(load(yamlFilePath)).asListAny

  private def load(file: String): Any =
    yaml.load(new FileInputStream(file))

  private def convertToScala(javaObject: Any): Any = javaObject match {

    case map: java.util.Map[Any, Any] =>
      val scalaMap = map.asScala
      scalaMap foreach { case (key, value) =>
        scalaMap(key) = convertToScala(value)
      }
      scalaMap.toMap

    case list: java.util.List[Any] =>
      val buffer = list.asScala
      buffer.map(convertToScala).toList

    case other =>
      other

  }

}
