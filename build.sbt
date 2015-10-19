
val orgName = "io.github.daviddenton"

val projectName = "databob"

organization := orgName

name := projectName

description := "Random test object generation"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-core" % "3.3.0",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)
