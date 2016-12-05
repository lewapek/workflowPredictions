package pl.edu.agh.workflowPerformance.utils

import pl.edu.agh.workflowPerformance.utils.AnyConversions.ConversionException

/**
  * @author lewap
  * @since 28.11.16
  */
trait AnyConversions {

  implicit class AnyWrapper(any: Any) {

    def asMapStringAny: Map[String, Any] = any match {
      case value: Map[String, Any]@unchecked => value
      case _ => throw ConversionException(s"Could not convert $any to Map[String, Any]")
    }

    def asListAny: List[Any] = any match {
      case value: List[Any]@unchecked => value
      case _ => throw ConversionException(s"Could not convert $any to List[Any]")
    }

    def asString: String = any match {
      case value: String => value
      case _ => throw ConversionException(s"Could not convert $any to String")
    }

    def asDouble: Double = any match {
      case value: Double => value
      case value: Int => value.toDouble
      case _ => throw ConversionException(s"Could not convert $any to Double")
    }

    def asInt: Int = any match {
      case value: Int => value
      case _ => throw ConversionException(s"Could not convert $any to Int")
    }

  }

}

object AnyConversions {

  case class ConversionException(message: String) extends Exception(message)

}
