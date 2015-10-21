package io.github.databob.generators

import java.sql.Timestamp
import java.time._
import java.util.Date

import io.github.databob.Generator._
import io.github.databob.Generators

object DefaultJavaDateTimeGenerators extends Generators(
  List(
    typeBased(databob => Instant.ofEpochMilli(0)),
    typeBased(databob => LocalDate.from(databob.random[Instant])),
    typeBased(databob => LocalTime.from(databob.random[Instant])),
    typeBased(databob => LocalDateTime.from(databob.random[Instant])),
    typeBased(databob => ZonedDateTime.from(databob.random[Instant])),
    erasureBased(databob => new Date(databob.random[Instant].toEpochMilli)),
    erasureBased(databob => new Timestamp(databob.random[Instant].toEpochMilli))
  )
)
