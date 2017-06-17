package pl.edu.agh.workflowPerformance.workflows.montage8Yaml

import pl.edu.agh.workflowPerformance.Settings
import Entities._
import pl.edu.agh.workflowPerformance.utils.{AnyConversions, YamlParser}

import scala.collection.mutable
import scala.language.postfixOps

/**
  * @author lewap
  * @since 08.11.16
  */
trait MontageYamlParser extends ParserFilters with AnyConversions with Settings {

  private val dataPrefix =  resourcesData("montageWorkflows/")

  private val montage = parse("montage.8.0.yaml")
  private val infrastructure = parse("infrastructure.yaml")
  private val workflow = parse("workflow.yaml")

  val instances = parseInstances()
  val tasks = parseTasks()
  val workflowTasks = parseWorkflowTasks()
  val execTimes = parseExecTimes()

  private def parse(filename: String): Map[String, Any] =
    YamlParser.mapStringAnyFrom(dataPrefix + filename)

  private def parseInstances(): Map[String, Instance] = {
    val instances = infrastructure("instances").asMapStringAny
    val result = mutable.Map.empty[String, Instance]
    instances foreach { case (name, parameters) =>
      val parametersMap = parameters.asMapStringAny
      val price = parametersMap("instance_price").asDouble
      val cores = parametersMap("cores").asInt
      val memoryGiB = parametersMap("mem").asDouble
      val network = parametersMap("network").asDouble

      result(name) = Instance(name, price, cores, memoryGiB, network)
    }
    result.toMap
  }

  private def parseTasks(): Map[String, Task] = {
    val tasks = montage("tasks").asMapStringAny
    val sequenceNumberIterator = (0 until tasks.size).iterator

    val result = mutable.Map.empty[String, Task]
    tasks foreach { case (name, parameters) =>
      val parametersMap = parameters.asMapStringAny
      val sequenceNumber = sequenceNumberIterator.next()
      val taskCount = parametersMap("task_count").asInt
      val dataSizeInput = parametersMap("data_size_in").asInt
      val dataSizeOutput = parametersMap("data_size_out").asDouble

      val taskExtension = Option(
        TaskExtension(List())
      )
      result(name) = Task(name, sequenceNumber, taskCount, dataSizeInput, dataSizeOutput, taskExtension)
    }
    result.toMap
  }

  private def parseWorkflowTasks(): Map[String, WorkflowTask] = {
    val tasks = workflow("tasks").asMapStringAny
    val sequenceNumberIterator = (0 until tasks.size).iterator

    val result = mutable.Map.empty[String, WorkflowTask]
    tasks foreach { case (name, parameters) =>
      val parametersMap = parameters.asMapStringAny
      val sequenceNumber = sequenceNumberIterator.next()
      val taskCount = parametersMap("task_count").asInt
      val singleExecTime = parametersMap("exec_time").asDouble
      val dataSizeInput = parametersMap("data_size_in").asDouble
      val dataSizeOutput = parametersMap("data_size_out").asDouble

      result(name) = WorkflowTask(name, sequenceNumber, taskCount, singleExecTime, dataSizeInput, dataSizeOutput)
    }
    result.toMap
  }

  private def parseExecTimes(): List[Performance] = {
    val times = montage("exec_time").asListAny
    times map { time =>
      val list = time.asListAny
      val taskName = list.head.asString
      val instanceName = list(1).asString
      val execTime = list(2).asDouble

      val instance = instances(instanceName)
      val performance = instance.cores / execTime
      Performance(tasks(taskName), workflowTasks.get(taskName), instances(instanceName), execTime, performance)
    }
  }

}
