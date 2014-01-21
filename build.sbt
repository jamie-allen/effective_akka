scalaVersion := "2.10.2"

name := "Effective Akka"

ScctPlugin.instrumentSettings

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.1.4",
  "com.typesafe.akka" %% "akka-slf4j" % "2.1.4",
  "com.typesafe.akka" %% "akka-testkit" % "2.1.4" % "test",
  "ch.qos.logback" % "logback-classic" % "1.0.10",
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test"
)

