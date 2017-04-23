package pl.edu.agh.workflowPerformance.workflows.logs.sns.structure

import pl.edu.agh.workflowPerformance.workflows.logs.regression.AbstractRow

/**
  * @author lewap
  * @since 16.02.17
  */
case class SnsTotalTimeRow(y: Double, atoms: Double, cores: Long, timesteps: Double, outputFrequency: Double)
  extends AbstractRow
