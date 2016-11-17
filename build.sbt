scalaVersion := "2.12.0"

name := "Effective Akka"

libraryDependencies ++= Seq(
          "com.typesafe.akka" %% "akka-actor" % "2.4.12",
          "com.typesafe.akka" %% "akka-slf4j" % "2.4.12",
          "ch.qos.logback" % "logback-classic" % "1.1.2",
          "com.typesafe.akka" %% "akka-testkit" % "2.4.12" % "test",
          "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

