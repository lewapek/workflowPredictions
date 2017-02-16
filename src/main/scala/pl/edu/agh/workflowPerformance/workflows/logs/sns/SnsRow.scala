package pl.edu.agh.workflowPerformance.workflows.logs.sns

import pl.edu.agh.workflowPerformance.workflows.logs.AbstractRow

/**
  * @author lewap
  * @since 16.02.17
  */
case class SnsRow(time: Double, atoms: Double, cores: Long, timesteps: Double, outputFrequency: Double)
  extends AbstractRow
