scalaVersion := "2.10.0"

name := "Effective Akka"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-optimize")
                  
credentials += Credentials(Path.userHome / "atmos.credentials")

libraryDependencies ++= Seq(
          "com.typesafe.akka" %% "akka-actor" % "2.1.0",
          "com.typesafe.akka" %% "akka-testkit" % "2.1.0" % "test",
          "org.scalatest" %% "scalatest" % "1.9.1",
          "junit" % "junit" % "4.7" % "test"
				  )

