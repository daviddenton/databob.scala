package io.github.databob

import io.github.databob.generators.Generators
import io.github.databob.generators.Generators._
import org.json4s.reflect.Reflector._
import org.json4s.reflect._

import scala.reflect.Manifest

class Databob(generators: Generators = new Generators()) {

  def mk[A](implicit mf: Manifest[A]): A = {
    try {
      mk(scalaTypeOf[A]).asInstanceOf[A]
    } catch {
      case e: GeneratorFailure => throw e
      case e: Exception => throw new GeneratorFailure(s"Generation error: ${e.getMessage}", e)
    }
  }

  private[databob] def mk(scalaType: ScalaType): Any = {
    val generatorType = GeneratorType(scalaType.typeInfo, scalaType.erasure, scalaType.typeArgs)
    val r = generators.pf(this)

    if (r.isDefinedAt(generatorType)) r(generatorType)
    else {
      describe(scalaType) match {
        case o: ClassDescriptor if o.erasure.isSingleton => o.erasure.singletonInstance.getOrElse(sys.error(s"Not r case object: ${o.erasure}"))
        case c: ClassDescriptor => new ClassInstanceBuilder(c).result
      }
    }
  }

  private class ClassInstanceBuilder(descr: ClassDescriptor) {
    private def instantiate = {
      try {
        val constructor = descr.constructors.headOption.getOrElse(throw new GeneratorFailure("No constructor found for type " + descr.erasure))
        constructor.constructor.invoke(descr.companion, constructor.params.map(a => mk(a.argType))).asInstanceOf[AnyRef]
      } catch {
        case e@(_: IllegalArgumentException | _: InstantiationException) => {
          e.printStackTrace()
          throw new GeneratorFailure("Could not construct class:"  + descr.fullName, e)
        }
      }
    }

    def result: Any = {
      val target = descr.erasure
      val generatorType = GeneratorType(target.typeInfo, target.erasure, target.typeArgs)
      val r = generators.pf(Databob.this)
      if (r.isDefinedAt(generatorType)) r(generatorType) else instantiate
    }
  }

}

/**
 * Main entry point for object generation
 */
object Databob {
  /**
   * Make an object using the set of generators provided with no fallbacks
   * @param generators the set of generators to use for the generation
   * @param mf manifest for the object to generate
   * @return the generated object
   */
  def mk[A](implicit generators: Generators = Empty, mf: Manifest[A]): A = new Databob(generators).mk[A]

  /**
   * Make a default object using the overrides provided and falling back to the set of Default generators
   * @param overrides the set of override generators to apply to the generation
   * @param mf manifest for the object to generate
   * @return the generated object
   */
  def default[A](implicit overrides: Generators = Empty, mf: Manifest[A]): A = mk[A](overrides ++ Defaults, mf)

  /**
   * Make a random object using the overrides provided and falling back to the set of Random generators
   * @param overrides the set of override generators to apply to the generation
   * @param mf manifest for the object to generate
   * @return the generated object
   */
  def random[A](implicit overrides: Generators = Empty, mf: Manifest[A]): A =  mk[A](overrides ++ Random, mf)
}
