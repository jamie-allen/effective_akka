scalaVersion := "2.12.2"

name := "Effective Akka"

libraryDependencies ++= Seq(
          "com.typesafe.akka" %% "akka-actor" % "2.5.1",
          "com.typesafe.akka" %% "akka-slf4j" % "2.5.1",
          "ch.qos.logback" % "logback-classic" % "1.2.3",
          "com.typesafe.akka" %% "akka-testkit" % "2.5.1" % "test",
          "org.scalatest" %% "scalatest" % "3.0.3" % "test"
)

