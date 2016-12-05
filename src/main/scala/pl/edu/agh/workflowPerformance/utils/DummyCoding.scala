package pl.edu.agh.workflowPerformance.utils

/**
  * @author lewap
  * @since 28.11.16
  */
trait DummyCoding {

  val sequenceNumber: Int

  def toDummyCoding(size: Int): List[AnyVal] = {
    if (sequenceNumber == 0) {
      List.fill(size)(0)
    } else {
      List.fill(size - sequenceNumber)(0) ::: 1 :: List.fill(sequenceNumber - 1)(0)
    }
  }

}
