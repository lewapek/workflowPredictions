package pl.edu.agh.workflowPerformance.workflows.logs.sns.structure

import pl.edu.agh.workflowPerformance.workflows.logs.regression.AbstractRow

/**
  * @author lewap
  * @since 16.02.17
  */
case class SnsProfileRow(y: Double,
                         atoms: Double,
                         cores: Double,
                         timesteps: Double,
                         outputFrequency: Double,
                         pointInTime: Double) extends AbstractRow
