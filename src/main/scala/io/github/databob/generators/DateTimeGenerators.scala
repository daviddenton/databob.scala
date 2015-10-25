package io.github.databob.generators

import java.sql.Timestamp
import java.time._
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.concurrent.TimeUnit

import io.github.databob.Databob
import io.github.databob.Generator._

/**
 * Generators for Date and Time types
 */
object DateTimeGenerators {

  /**
   * Creates Date and Time types where the instant is at the Epoch
   */
  lazy val Epoch = typeIs(databob => Instant.ofEpochMilli(0)) +
    typeIs(databob => ZoneId.of("UTC")) +
    typeIs(databob => LocalDate.from(databob.mk[ZonedDateTime])) +
    typeIs(databob => LocalTime.from(databob.mk[ZonedDateTime])) +
    typeIs(databob => LocalDateTime.from(databob.mk[ZonedDateTime])) +
    typeIs(databob => ZonedDateTime.ofInstant(databob.mk[Instant], databob.mk[ZoneId])) +
    typeIs(databob => new Date(databob.mk[Instant].toEpochMilli)) +
    typeIs(databob => new Timestamp(databob.mk[Instant].toEpochMilli)) +
    typeIs(databob => Duration.of(databob.mk[Instant].toEpochMilli, ChronoUnit.MILLIS)) +
    erasureIs(databob => {
      println("HELLO")
      scala.concurrent.duration.Duration(databob.mk[Instant].toEpochMilli, TimeUnit.MILLISECONDS)
    })

  /**
   * Creates Random Date and Time instances (no boundaries)
   */
  lazy val Random = typeIs(databob => Instant.ofEpochMilli(Databob.random[Long])) +: Epoch
}
