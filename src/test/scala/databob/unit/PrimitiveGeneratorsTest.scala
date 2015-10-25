package databob.unit

import java.lang.{Boolean => JavaBoolean, Byte => JavaByte, Character => JavaChar, Double => JavaDouble, Float => JavaFloat, Integer => JavaInteger, Long => JavaLong, Short => JavaShort, String => JavaString}
import java.math.{BigDecimal => JavaBigDecimal, BigInteger => JavaBigInteger}

import io.github.databob.Databob
import io.github.databob.generators._
import org.scalatest.{FunSpec, ShouldMatchers}

import scala.reflect.Manifest

class PrimitiveGeneratorsTest extends FunSpec with ShouldMatchers {

  describe("makes") {
    def itSupports[A: Manifest](expected: Any)(implicit generators: Generators, mf: Manifest[A]): Unit = {
      it(mf.runtimeClass.getName + " at " + System.nanoTime()) {
        Databob.mk[A](generators, mf) shouldBe expected
      }
    }

    def itSupportsRandom[A: Manifest](implicit generators: Generators, mf: Manifest[A]): Unit = {
      it(mf.runtimeClass.getName + " at " + System.nanoTime()) {
        Range(1, 10).map(i => Databob.mk[A](generators, mf)).toSet.size should not be 1
      }
    }

    describe("default") {
      implicit val g = PrimitiveGenerators.Defaults
      itSupports[Int](0)
      itSupports[JavaInteger](0)

      itSupports[Long](0)
      itSupports[JavaLong](0L)

      itSupports[Double](0)
      itSupports[JavaDouble](0d)

      itSupports[Float](0)
      itSupports[JavaFloat](0f)

      itSupports[Short](0)
      itSupports[JavaShort](0.toShort)

      itSupports[Byte](0)
      itSupports[JavaByte](0.toByte)

      itSupports[Boolean](false)
      itSupports[JavaBoolean](false)

      itSupports[Char](0.toChar)
      itSupports[JavaChar](0.toChar)

      itSupports[scala.BigDecimal](BigDecimal(0))
      itSupports[JavaBigDecimal](BigDecimal(0))

      itSupports[BigInt](BigInt(0))
      itSupports[JavaBigInteger](BigInt(0))

      itSupports[String]("")
      itSupports[JavaString]("")
    }

    describe("random") {
      implicit val g = PrimitiveGenerators.Random
      itSupportsRandom[Int]
      itSupportsRandom[JavaInteger]

      itSupportsRandom[Long]
      itSupportsRandom[JavaLong]

      itSupportsRandom[Double]
      itSupportsRandom[JavaDouble]

      itSupportsRandom[Float]
      itSupportsRandom[JavaFloat]

      itSupportsRandom[Short]
      itSupportsRandom[JavaShort]

      itSupportsRandom[Byte]
      itSupportsRandom[JavaByte]

      itSupportsRandom[Boolean]
      itSupportsRandom[JavaBoolean]

      itSupportsRandom[Char]
      itSupportsRandom[JavaChar]

      itSupportsRandom[scala.BigDecimal]
      itSupportsRandom[JavaBigDecimal]

      itSupportsRandom[BigInt]
      itSupportsRandom[JavaBigInteger]

      itSupportsRandom[String]
      itSupportsRandom[JavaString]
    }
  }
}
