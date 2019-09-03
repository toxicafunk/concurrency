name := "Scala Concurrency"

version := "0.1"

scalaVersion := "2.12.8"
//scalaVersion := "2.11.12"

fork in run := true

libraryDependencies += "io.monix" %% "monix" % "3.0.0-RC5"


// better type inference when multiple type parameters are involved and they need to be inferred in multiple steps
// i.e. conversion from Function1[Int, Int] to (approximately) Function1[Int][Int]
scalacOptions ++= Seq(
  "-feature",
  "-Ypartial-unification",
  "-Ywarn-value-discard"
)

//javaOptions += "-agentpath:/usr/share/visualvm/profiler/lib/deployed/jdk16/linux-amd64/libprofilerinterface.so=/usr/share/visualvm/profiler/lib,5140"

