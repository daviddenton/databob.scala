package databob.unit

import java.lang.{Boolean => JavaBoolean, Byte => JavaByte, Character => JavaChar, Double => JavaDouble, Float => JavaFloat, Integer => JavaInteger, Long => JavaLong, Short => JavaShort, String => JavaString}
import java.math.{BigDecimal => JavaBigDecimal, BigInteger => JavaBigInteger}

import io.github.databob.Databob
import io.github.databob.generators._
import org.scalatest.{FunSpec, ShouldMatchers}

import scala.reflect.Manifest

trait GeneratorSpecs {
  self: FunSpec with ShouldMatchers =>
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
}