package databob.unit

import java.lang.{Boolean => JavaBoolean, Byte => JavaByte, Double => JavaDouble, Float => JavaFloat, Integer => JavaInteger, Long => JavaLong, Short => JavaShort, String => JavaString}
import java.math.{BigDecimal => JavaBigDecimal, BigInteger => JavaBigInteger}
import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime, LocalTime, ZonedDateTime}
import java.util.Date

import io.github.databob._
import io.github.databob.generators.Generators._
import io.github.databob.generators._
import org.scalatest.{FunSpec, ShouldMatchers}

import scala.reflect.Manifest

case class YetAnother(name: Int, which: Boolean, time: LocalDateTime)

case class Other(name: String, yet: YetAnother)

case class Person(other: Other, age: Option[ZonedDateTime], bob: LocalDate, names: Seq[Other], aMap: Map[String, ZonedDateTime])

class APrivateClass private()

class DatabobTest extends FunSpec with ShouldMatchers {

  def describe(name: String, generators: Generators): Unit = {

    def itSupports[A: Manifest](implicit mf: Manifest[A]): Unit = {
      it(name + " : " + mf.runtimeClass.getSimpleName) {
        Databob.mk[A](generators, mf) === null shouldBe false
      }
    }

    describe(name) {
      describe(JavaPrimitiveGenerators.getClass.getSimpleName) {
        itSupports[JavaInteger]
        itSupports[JavaBigDecimal]
        itSupports[JavaBigInteger]
        itSupports[JavaLong]
        itSupports[JavaFloat]
        itSupports[JavaShort]
        itSupports[JavaString]
        itSupports[JavaDouble]
        itSupports[JavaByte]
        itSupports[JavaBoolean]
      }

      describe(JavaDateTimeGenerators.getClass.getSimpleName) {
        itSupports[LocalDate]
        itSupports[LocalTime]
        itSupports[LocalDateTime]
        itSupports[ZonedDateTime]
        itSupports[Date]
        itSupports[Timestamp]
      }

      describe(ScalaPrimitiveGenerators.getClass.getSimpleName) {
        itSupports[Int]
        itSupports[Long]
        itSupports[Double]
        itSupports[BigDecimal]
        itSupports[BigInt]
        itSupports[Float]
        itSupports[Short]
        itSupports[Byte]
        itSupports[Boolean]
      }

      describe(CollectionGenerators.getClass.getSimpleName) {
        itSupports[List[Int]]
        itSupports[Map[Int, Int]]
        itSupports[Set[Int]]
        itSupports[Seq[Int]]
        itSupports[Array[Int]]
        itSupports[Vector[Int]]
      }

      describe(MonadGenerators.getClass.getSimpleName) {
        itSupports[Option[Int]]
        itSupports[Either[Int, String]]
      }

      describe("Custom case classes") {
        itSupports[Person]
      }
    }
  }

  describe("default", Default)
  describe("random", Random)

  describe("Custom generator") {
    it("is used") {
      val custom = LocalTime.of(12, 12, 12)
      implicit val r = Generator(databob => custom) +: Default
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
