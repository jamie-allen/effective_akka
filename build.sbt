scalaVersion := "2.10.0"

name := "Effective Akka"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-optimize")

resolvers ++= Seq("akka-snapshots" at "http://repo.akka.io/snapshots",
                  "akka-releases"  at "http://repo.akka.io/releases",
									"Atmos Repo" at "http://repo.typesafe.com/typesafe/atmos-releases/",
                  "releases"  at "http://oss.sonatype.org/content/repositories/releases")
                  
credentials += Credentials(Path.userHome / "atmos.credentials")

libraryDependencies ++= Seq(
//				  "com.typesafe.atmos" % "atmos-akka-actor_2.10.0" % "2.1.0",
          "com.typesafe.akka" % "akka-actor_2.10" % "2.1.0",
          "com.typesafe.akka" % "akka-testkit_2.10" % "2.1.0" % "test",
          "org.scalatest" % "scalatest_2.10" % "1.9.1",
          "junit" % "junit" % "4.7" % "test"
				  )

