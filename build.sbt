import _root_.bintray.Plugin._

val orgName = "io.github.daviddenton"

val projectName = "databob"

organization := orgName

name := projectName

description := "Randomized object generation"

scalaVersion := "2.11.7"

scalacOptions += "-deprecation"

scalacOptions += "-feature"

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-core" % "3.3.0",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test"
)

licenses +=("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))

pomExtra :=
  <url>http://{projectName}.github.io/</url>
    <scm>
      <url>git@github.com:daviddenton/{projectName}.scala.git</url>
      <connection>scm:git:git@github.com:daviddenton/{projectName}.scala.git</connection>
      <developerConnection>scm:git:git@github.com:daviddenton/{projectName}.scala.git</developerConnection>
    </scm>
    <developers>
      <developer>
        <name>David Denton</name>
        <email>dev@fintrospect.io</email>
        <organization>{projectName}</organization>
        <organizationUrl>http://daviddenton.github.io</organizationUrl>
      </developer>
    </developers>

Seq(bintraySettings: _*)
