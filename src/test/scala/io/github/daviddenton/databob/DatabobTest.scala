package io.github.daviddenton.databob

import java.lang.{Boolean => JavaBoolean, Byte => JavaByte, Double => JavaDouble, Float => JavaFloat, Integer => JavaInteger, Long => JavaLong, Short => JavaShort, String => JavaString}
import java.math.{BigDecimal => JavaBigDecimal, BigInteger => JavaBigInteger}
import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime, LocalTime, ZonedDateTime}
import java.util.Date

import io.github.databob._
import io.github.databob.generators._
import org.scalatest.{FunSpec, ShouldMatchers}

import scala.reflect.Manifest

case class YetAnother(name: Int, which: Boolean, time: LocalDateTime)

case class Other(name: String, yet: YetAnother)

case class Person(other: Other, age: Option[ZonedDateTime], bob: LocalDate, names: Seq[Other], aMap: Map[String, ZonedDateTime])

class APrivateClass private()

class DatabobTest extends FunSpec with ShouldMatchers {

  private def itSupports[A: Manifest](implicit mf: Manifest[A]): Unit = {
    it(mf.runtimeClass.getSimpleName) {
      Databob.default[A](DefaultGenerators, mf) === null shouldBe false
    }
  }

  describe(DefaultJavaPrimitiveGenerators.getClass.getSimpleName) {
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

  describe(DefaultJavaDateTimeGenerators.getClass.getSimpleName) {
    itSupports[LocalDate]
    itSupports[LocalTime]
    itSupports[LocalDateTime]
    itSupports[ZonedDateTime]
    itSupports[Date]
    itSupports[Timestamp]
  }

  describe(DefaultScalaPrimitiveGenerators.getClass.getSimpleName) {
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

  describe(EmptyCollectionGenerators.getClass.getSimpleName) {
    itSupports[List[Int]]
    itSupports[Map[Int, Int]]
    itSupports[Set[Int]]
    itSupports[Seq[Int]]
    itSupports[Array[Int]]
    itSupports[Vector[Int]]
  }

  describe(DefaultMonadGenerators.getClass.getSimpleName) {
    itSupports[Option[Int]]
    itSupports[Either[Int, String]]
  }

  describe("Custom case classes") {
    itSupports[Person]
  }

  describe("Custom generator") {
    it("is used") {
      val custom = LocalTime.of(12, 12, 12)
      implicit val r = Generator(databob => custom) +: DefaultGenerators
      Databob.default[LocalTime] shouldBe custom
    }
  }

  describe("Failure cases") {
    it("Blows up when there are no generators") {
      implicit val r = new Generators()
      intercept[GeneratorFailure](Databob.default[Int])
    }

    it("Blows up when there are no constructor to call") {
      intercept[GeneratorFailure](Databob.default[APrivateClass])
    }
  }
}
