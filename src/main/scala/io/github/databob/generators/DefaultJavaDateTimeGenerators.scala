package io.github.databob.generators

import java.sql.Timestamp
import java.time._
import java.util.Date

import io.github.databob.Generator._
import io.github.databob.Generators

object DefaultJavaDateTimeGenerators extends Generators(
  List(
    typeBased(databob => LocalDate.of(2000, 1, 1)),
    typeBased(databob => LocalTime.of(0, 0, 0)),
    typeBased(databob => LocalDateTime.of(databob.random[LocalDate], databob.random[LocalTime])),
    typeBased(databob => ZonedDateTime.of(databob.random[LocalDateTime], ZoneId.systemDefault())),
    erasureBased(databob => new Date(0)),
    erasureBased(databob => new Timestamp(0))
  )
)
