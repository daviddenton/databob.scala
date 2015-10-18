package io.github.daviddenton.databob

import java.time.{LocalDate, LocalDateTime, LocalTime, ZonedDateTime}

import io.github.databob.Databob.random
import io.github.databob.{Randomizer, Randomizers}

case class YetAnother(name: Int, which: Boolean, time: LocalDateTime)

case class Other(name: String, yet: YetAnother)

case class Person(other: Other, age: Option[ZonedDateTime], bob: LocalDate, names: Seq[Other], aMap: Map[String, ZonedDateTime])

object Test extends App {
  // fix this so it always passes through the constant set of randomizers (ie. they are locked into the databob instance˚ as a val)
  implicit val f = Randomizers() + Randomizer(databob => LocalDateTime.of(databob.random[LocalDate], LocalTime.of(12, 12, 12))) + Randomizer(r => LocalDate.of(2010, 12, 1))
  println(random[Person])
}
