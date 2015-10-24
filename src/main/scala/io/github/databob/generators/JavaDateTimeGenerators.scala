package io.github.databob.generators

import java.sql.Timestamp
import java.time._
import java.util.Date

import io.github.databob.Generator._

object JavaDateTimeGenerators {
  val Default = new Generators(
    List(
      typeIs(databob => Instant.ofEpochMilli(0)),
      typeIs(databob => LocalDate.from(databob.mk[ZonedDateTime])),
      typeIs(databob => LocalTime.from(databob.mk[ZonedDateTime])),
      typeIs(databob => LocalDateTime.from(databob.mk[ZonedDateTime])),
      typeIs(databob => ZonedDateTime.ofInstant(databob.mk[Instant], ZoneId.systemDefault())),
      erasureIs(databob => new Date(databob.mk[Instant].toEpochMilli)),
      erasureIs(databob => new Timestamp(databob.mk[Instant].toEpochMilli))
    )
  )

  val Random = typeIs(databob => Instant.ofEpochMilli(0)) +: Default
}
