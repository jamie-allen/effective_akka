scalaVersion := "2.10.0-RC5"

name := "Effective Akka"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-optimize")

resolvers ++= Seq("akka-snapshots" at "http://repo.akka.io/snapshots",
                  "akka-releases"  at "http://repo.akka.io/releases",
									"Atmos Repo" at "http://repo.typesafe.com/typesafe/atmos-releases/",
                  "releases"  at "http://oss.sonatype.org/content/repositories/releases")
                  
credentials += Credentials(Path.userHome / "atmos.credentials")

libraryDependencies ++= Seq(
//				  "com.typesafe.atmos" % "atmos-akka-actor_2.10.0-RC1" % "2.1.0-RC1",
				  "com.typesafe.akka" % "akka-actor_2.10.0-RC1" % "2.1.0-RC1",
          "com.typesafe.akka" % "akka-testkit_2.10.0-RC1" % "2.1.0-RC1" % "test",
          "org.scalatest" % "scalatest_2.10.0-RC1" % "1.8-2.10.0-RC1-B1",
          "junit" % "junit" % "4.7" % "test"
				  )

