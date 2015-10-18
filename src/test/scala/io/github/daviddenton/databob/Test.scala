package io.github.daviddenton.databob

import java.time.{ZonedDateTime, LocalDate}
import java.util.Date

import io.github.databob.Databob.random
import io.github.databob.Randomizers

case class YetAnother(name: Int, which: Boolean, time: Date)
case class Other(name: String, yet: YetAnother)

case class Person(other: Other, age: Option[ZonedDateTime], bob: LocalDate, names: Seq[Other], aMap: Map[String, ZonedDateTime])

object Test extends App {
  implicit val f = Randomizers()
  println(random[Person])
}
