name := "workflowPredictions"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.yaml" % "snakeyaml" % "1.17",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "org.deeplearning4j" % "deeplearning4j-core" % "0.7.2",
  "jfree" % "jfreechart" % "1.0.13",
  "org.nd4j" % "nd4j-native" % "0.7.2",
  "org.bytedeco" % "javacpp" % "1.3.1"
)
