package databob.unit

import java.sql.Timestamp
import java.time._
import java.util.Date

import io.github.databob.generators._
import org.scalatest.{FunSpec, Matchers}

class DateTimeGeneratorsTest extends FunSpec with Matchers with GeneratorSpecs {

  describe("default") {
    implicit val g = DateTimeGenerators.Epoch
    itSupports[Instant](Instant.ofEpochMilli(0))
    itSupports[LocalDate](LocalDate.of(1970, 1, 1))
    itSupports[LocalTime](LocalTime.of(0, 0, 0))
    itSupports[LocalDateTime](LocalDateTime.of(1970, 1, 1, 0, 0, 0))
    itSupports[ZonedDateTime](ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")))
    itSupports[Date](new Date(0))
    itSupports[Timestamp](new Timestamp(0))
    itSupports[Duration](Duration.ofMillis(0))
    itSupports[Period](Period.ofDays(0))
  }

  describe("random") {
    implicit val g = DateTimeGenerators.Random
    itSupportsRandom[Instant]
    itSupportsRandom[LocalDate]
    itSupportsRandom[LocalTime]
    itSupportsRandom[LocalDateTime]
    itSupportsRandom[ZonedDateTime]
    itSupportsRandom[Date]
    itSupportsRandom[Timestamp]
    itSupportsRandom[Duration]
    itSupportsRandom[Period]
  }
}
