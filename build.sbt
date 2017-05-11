name := "workflowPredictions"

version := "1.1"

scalaVersion := "2.12.2"

val logging = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.2.1",
  "org.codehaus.groovy" % "groovy-all" % "2.4.9",
  "org.codehaus.janino" % "janino" % "3.0.6"
)

val other = Seq(
  "org.yaml" % "snakeyaml" % "1.17"
)

libraryDependencies ++= logging ++ other
