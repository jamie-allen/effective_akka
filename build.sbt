scalaVersion := "2.11.8"

name := "Effective Akka"

libraryDependencies ++= Seq(
          "com.typesafe.akka" %% "akka-actor" % "2.4.8",
          "com.typesafe.akka" %% "akka-slf4j" % "2.4.8",
          "ch.qos.logback" % "logback-classic" % "1.1.2",
          "com.typesafe.akka" %% "akka-testkit" % "2.4.8" % "test",
          "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

