package io.github.daviddenton.databob

import io.github.databob.Databob.random
import io.github.databob.Randomizers

case class YetAnother(name: String)
case class Other(name: String, yet: YetAnother)

case class Person(other: Other, age: Int, bob: String, names: Seq[Other], aMap: Map[String, String])

object Test extends App {
  implicit val f = Randomizers()
  println(random[Person])
}
