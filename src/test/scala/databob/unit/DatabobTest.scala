package databob.unit

import java.time.{LocalDate, LocalDateTime, LocalTime, ZonedDateTime}

import io.github.databob._
import io.github.databob.generators.Generators._
import io.github.databob.generators._
import org.scalatest.{FunSpec, Matchers}

case class YetAnother(name: Int, which: Boolean, time: LocalDateTime)

case class Other(name: Option[String], yet: YetAnother)

case class Person(other: Other, age: Option[ZonedDateTime], bob: LocalDate, names: Seq[Other], aMap: Map[String, ZonedDateTime])

class APrivateClass private()

class DatabobTest extends FunSpec with Matchers with GeneratorSpecs {

  describe("Custom classes") {
    describe("default") {
      it("supports nested object trees") {
        Databob.default[Person] should not be null
      }
    }

    describe("random") {
      implicit val g = Random
      itSupportsRandom[Person]
      itSupportsRandom[Other]
    }
  }

  describe("Custom generator") {
    it("is used") {
      val custom = LocalTime.of(12, 12, 12)
      implicit val r = Generator(databob => custom) +: Defaults
      Databob.mk[LocalTime] shouldBe custom
    }
  }

  describe("Failure cases") {
    it("Blows up when there are no generators") {
      implicit val r = new Generators()
      intercept[GeneratorFailure](Databob.mk[Int])
    }

    it("Blows up when there are no constructor to call") {
      implicit val r = new Generators()
      intercept[GeneratorFailure](Databob.mk[APrivateClass])
    }
  }
}
