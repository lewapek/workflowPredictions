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
  "org.yaml" % "snakeyaml" % "1.17",
  "com.typesafe" % "config" % "1.3.1"
)

libraryDependencies ++= logging ++ other

//mainClass in Compile := Some("")

resourceDirectory in Compile := baseDirectory.value / "conf"

resourceDirectory in Test := baseDirectory.value / "conf"

unmanagedResourceDirectories in Compile += baseDirectory.value / "src/main/resources"

unmanagedResourceDirectories in Test += baseDirectory.value / "src/main/resources"
