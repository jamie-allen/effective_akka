scalaVersion := "2.10.0"

name := "Effective Akka"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-optimize")

credentials += Credentials(Path.userHome / "atmos.credentials")

seq(ScctPlugin.instrumentSettings : _*)

libraryDependencies ++= Seq(
          "com.typesafe.akka" %% "akka-actor" % "2.1.2",
          "com.typesafe.akka" %% "akka-slf4j" % "2.1.2",
          "com.typesafe.akka" %% "akka-testkit" % "2.1.2" % "test",
          "ch.qos.logback" % "logback-classic" % "1.0.10",
				  "org.scalatest" %% "scalatest" % "2.0.M6-SNAP14" % "test"
				  )

