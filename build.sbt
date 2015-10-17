
val orgName = "io.github.daviddenton"

val projectName = "databob"

organization := orgName

name := projectName

description := "Random test object generation"

scalaVersion := "2.11.7"

//crossScalaVersions := Seq("2.10.4", "2.11.5")

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-core" % "3.3.0",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

//licenses +=("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))
//
//pomExtra :=
//  <url>http://daviddenton.github.io/databob.scala</url>
//    <scm>
//      <url>git@github.com:daviddenton/databob.scala.git</url>
//      <connection>scm:git:git@github.com:daviddenton/databob.scala.git</connection>
//      <developerConnection>scm:git:git@github.com:daviddenton/databob.scala.git</developerConnection>
//    </scm>
//    <developers>
//      <developer>
//        <name>David Denton</name>
//        <email>mail@daviddenton.github.io</email>
//        <organization>databob</organization>
//        <organizationUrl>http://daviddenton.github.io</organizationUrl>
//      </developer>
//    </developers>
//
//Seq(bintraySettings: _*)
