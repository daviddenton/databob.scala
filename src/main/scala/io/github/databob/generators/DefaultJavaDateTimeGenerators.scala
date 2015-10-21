package io.github.databob.generators

import java.sql.Timestamp
import java.time._
import java.util.Date

import io.github.databob.Generator._
import io.github.databob.Generators

object DefaultJavaDateTimeGenerators extends Generators(
  List(
    typeBased(databob => Instant.ofEpochMilli(0)),
    typeBased(databob => LocalDate.from(databob.mk[ZonedDateTime])),
    typeBased(databob => LocalTime.from(databob.mk[ZonedDateTime])),
    typeBased(databob => LocalDateTime.from(databob.mk[ZonedDateTime])),
    typeBased(databob => ZonedDateTime.ofInstant(databob.mk[Instant], ZoneId.systemDefault())),
    erasureBased(databob => new Date(databob.mk[Instant].toEpochMilli)),
    erasureBased(databob => new Timestamp(databob.mk[Instant].toEpochMilli))
  )
)
