# Workflow predictions

This repository was used to evaluate results presented in master thesis "Application of machine learning to performance modeling in clouds".

## Getting started

In order to run this project you need to meet the following requirements:
* sbt,
* python3 scikit-learn package,
* python3 matplotlib package,
* octave.

To run Montage workflow execution time prediction execute
```
sbt "run-main pl.edu.agh.workflowPerformance.workflows.logs.montage.MontageTasksRegressionRunner"
```

To run SNS workflow execution time prediction execute
```
sbt "run-main pl.edu.agh.workflowPerformance.workflows.logs.sns.SnsTasksTotalTimeRegressionRunner"
```

To run SNS workflow performance profile prediction execute
```
sbt "run-main pl.edu.agh.workflowPerformance.workflows.logs.sns.SnsTasksTimeProfileRegressionRunner"
```
