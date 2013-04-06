scalaVersion := "2.10.0"

name := "Effective Akka"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-optimize")

credentials += Credentials(Path.userHome / "atmos.credentials")

seq(ScctPlugin.instrumentSettings : _*)

libraryDependencies ++= Seq(
          "com.typesafe.akka" %% "akka-actor" % "2.1.0",
          "com.typesafe.akka" %% "akka-slf4j" % "2.1.0",
          "ch.qos.logback" % "logback-classic" % "1.0.10",
          "com.typesafe.akka" %% "akka-testkit" % "2.1.0" % "test",
          "org.scalatest" %% "scalatest" % "1.9.1" % "test",
          "junit" % "junit" % "4.7" % "test"
				  )

