package io.github.daviddenton.databob

import java.lang.{Boolean => JavaBoolean, Byte => JavaByte, Double => JavaDouble, Float => JavaFloat, Integer => JavaInteger, Long => JavaLong, Short => JavaShort, String => JavaString}
import java.math.{BigDecimal => JavaBigDecimal, BigInteger => JavaBigInteger}
import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime, LocalTime, ZonedDateTime}
import java.util.Date

import io.github.databob._
import org.scalatest.{FunSpec, ShouldMatchers}

import scala.reflect.Manifest

case class YetAnother(name: Int, which: Boolean, time: LocalDateTime)

case class Other(name: String, yet: YetAnother)

case class Person(other: Other, age: Option[ZonedDateTime], bob: LocalDate, names: Seq[Other], aMap: Map[String, ZonedDateTime])

class APrivateClass private()

class DatabobTest extends FunSpec with ShouldMatchers {

  private def itSupports[A: Manifest](implicit mf: Manifest[A]): Unit = {
    it(mf.runtimeClass.getSimpleName) {
      Databob.random[A](DefaultRandomizers, mf) === null shouldBe false
    }
  }

  describe(JavaPrimitiveRandomizers.getClass.getSimpleName) {
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

  describe(JavaDateTimeRandomizers.getClass.getSimpleName) {
    itSupports[LocalDate]
    itSupports[LocalTime]
    itSupports[LocalDateTime]
    itSupports[ZonedDateTime]
    itSupports[Date]
    itSupports[Timestamp]
  }

  describe(ScalaPrimitiveRandomizers.getClass.getSimpleName) {
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

  describe(CollectionRandomizers.getClass.getSimpleName) {
    itSupports[List[Int]]
    itSupports[Map[Int, Int]]
    itSupports[Set[Int]]
    itSupports[Seq[Int]]
    itSupports[Array[Int]]
    itSupports[Vector[Int]]
  }

  describe(MonadRandomizers.getClass.getSimpleName) {
    itSupports[Option[Int]]
    itSupports[Either[Int, String]]
  }

  describe("Custom case classes") {
    itSupports[Person]
  }

  describe("Custom randomizer") {
    it("is used") {
      val custom = LocalTime.of(12, 12, 12)
      implicit val r = Randomizers() + Randomizer(databob => custom)
      Databob.random[LocalTime] shouldBe custom
    }
  }

  describe("Failure cases") {
    it("Blows up when there are no randomizers") {
      implicit val r = Randomizers()
      intercept[RandomFailure](Databob.random[Int])
    }

    it("Blows up when there are no constructor to call") {
      implicit val r = Randomizers()
      intercept[RandomFailure](Databob.random[APrivateClass])
    }
  }
}
