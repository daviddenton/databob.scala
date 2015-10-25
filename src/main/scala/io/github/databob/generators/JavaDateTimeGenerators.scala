package io.github.databob.generators

import java.sql.Timestamp
import java.time._
import java.util.Date

import io.github.databob.Generator._

/**
 * Generators for Java Date and Time types
 */
object JavaDateTimeGenerators {

  /**
   * Creates Date and Time types where the instant is at the Epoch
   */
  lazy val Default = typeIs(databob => Instant.ofEpochMilli(0)) +
    typeIs(databob => LocalDate.from(databob.mk[ZonedDateTime])) +
    typeIs(databob => LocalTime.from(databob.mk[ZonedDateTime])) +
    typeIs(databob => LocalDateTime.from(databob.mk[ZonedDateTime])) +
    typeIs(databob => ZonedDateTime.ofInstant(databob.mk[Instant], ZoneId.systemDefault())) +
    erasureIs(databob => new Date(databob.mk[Instant].toEpochMilli)) +
    erasureIs(databob => new Timestamp(databob.mk[Instant].toEpochMilli))

  /**
   * Creates Random Date and Time instances (no boundaries)
   */
  lazy val Random = typeIs(databob => Instant.ofEpochMilli(0)) +: Default
}
