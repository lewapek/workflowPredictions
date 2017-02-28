package pl.edu.agh.workflowPerformance.workflows.montage8Yaml

import Entities.{Instance, Performance}

/**
  * @author lewap
  * @since 11.12.16
  */
trait ParserFilters {
  this: MontageYamlParser =>

  val t1MicroName = "t1-micro"

  def instancesWithoutT1Micro: Map[String, Instance] = {
    instancesWithout(t1MicroName)
  }

  def execTimesWithoutInstanceT1Micro: List[Performance] = {
    execTimesWithoutInstances(t1MicroName)
  }

  def instancesWithout(instanceName: String): Map[String, Instance] = {
    instances filter { case (name, instance) =>
      name != instanceName
    }
  }

  def execTimesWithoutInstances(names: String*): List[Performance] = {
    val namesSet = names.toSet
    execTimes.filterNot(row => namesSet.contains(row.instance.name))
  }

  def execTimesWithInstances(names: String*): List[Performance] = {
    val namesSet = names.toSet
    execTimes.filter(row => namesSet.contains(row.instance.name))
  }

  def execTimesWithoutTasks(names: String*): List[Performance] = {
    val namesSet = names.toSet
    execTimes.filterNot(row => namesSet.contains(row.task.name))
  }

  def execTimesWithTasks(names: String*): List[Performance] = {
    val namesSet = names.toSet
    execTimes.filter(row => namesSet.contains(row.task.name))
  }

}
