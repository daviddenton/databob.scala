package io.github.daviddenton.databob

import java.time.{LocalDate, LocalDateTime, LocalTime, ZonedDateTime}

import io.github.databob.Databob.random
import io.github.databob.{Randomizer, Randomizers}

case class YetAnother(name: Int, which: Boolean, time: LocalDateTime)
case class Other(name: String, yet: YetAnother)

case class Person(other: Other, age: Option[ZonedDateTime], bob: LocalDate, names: Seq[Other], aMap: Map[String, ZonedDateTime])

object Test extends App {
  implicit val f = Randomizers() + Randomizer(r => LocalTime.of(10, 1, 1))
  println(random[Person])
}
