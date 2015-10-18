package io.github.daviddenton.databob

import io.github.databob.{Random, RandomFormats}
import org.json4s.JsonAST.JObject

case class YetAnother(name: String)
case class Other(name: String, yet: YetAnother)

case class Person(other: Other, age: Int, bob: String, names: Seq[Other], aMap: Map[String, String])

object Test extends App {
  implicit val f = RandomFormats()
  println(Random.random[Person](JObject()))
}
