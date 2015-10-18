package org.json4s

case class Other(name: String)

case class Person(age: Int, bob: String, names: Seq[Other], aMap: Map[String, String])

object Test extends App {
  implicit val f = DefaultRandomFormats
  println(Random.random[Person](JObject()))
}
