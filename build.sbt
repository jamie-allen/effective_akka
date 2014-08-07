scalaVersion := "2.11.2"

name := "Effective Akka"

//seq(ScctPlugin.instrumentSettings : _*)

libraryDependencies ++= Seq(
          "com.typesafe.akka" %% "akka-actor" % "2.3.4",
          "com.typesafe.akka" %% "akka-slf4j" % "2.3.4",
          "ch.qos.logback" % "logback-classic" % "1.1.2",
          "com.typesafe.akka" %% "akka-testkit" % "2.3.4" % "test",
          "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

