package databob.unit

import io.github.databob.Databob
import io.github.databob.generators._
import org.scalatest.{FunSpec, Matchers}

import scala.reflect.Manifest

trait GeneratorSpecs {
  self: FunSpec with Matchers =>
  def itSupports[A: Manifest](expected: Any)(implicit generators: Generators, mf: Manifest[A]): Unit = {
    it(mf.runtimeClass.getName + " at " + System.nanoTime()) {
      Databob.mk[A](generators, mf) shouldBe expected
    }
  }

  def itSupportsRandom[A: Manifest](implicit generators: Generators, mf: Manifest[A]): Unit = {
    it(mf.runtimeClass.getName + " at " + System.nanoTime()) {
      Range(1, 50).map(i => Databob.mk[A](generators, mf)).toSet.size should not be 1
    }
  }
}