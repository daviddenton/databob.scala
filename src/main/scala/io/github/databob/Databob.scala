/**
 * Reflector logic taken from Json4S
 */
package io.github.databob

import org.json4s.reflect._

import scala.reflect.Manifest
import scala.util.control.Exception.allCatch

case class RandomFailure(msg: String) extends Exception(msg)

class Databob(randomizers: Randomizers = Randomizers()) {

  def random[A](implicit mf: Manifest[A]): A = {
    try {
      random(Reflector.scalaTypeOf[A]).asInstanceOf[A]
    } catch {
      case e: RandomFailure => throw e
      case e: Exception =>
        throw new RandomFailure("unknown error" + e.getMessage)
    }
  }

  private def random(scalaType: ScalaType): Any = {
    if (scalaType.isEither) {
      (allCatch opt {
        Left(random(scalaType.typeArgs.head))
      } orElse (allCatch opt {
        Right(random(scalaType.typeArgs(1)))
      })).getOrElse(throw new RandomFailure("Expected value but got none"))
    }
    else if (scalaType.isOption) Option(random(scalaType.typeArgs.head))
    else if (scalaType.isMap) Map()
    else if (scalaType.isCollection) {
      customOrElse(scalaType)(() => new CollectionBuilder(scalaType).result)
    } else {
      Reflector.describe(scalaType) match {
        case PrimitiveDescriptor(tpe, default) => convert(tpe, randomizers)
        case o: ClassDescriptor if o.erasure.isSingleton =>
          o.erasure.singletonInstance.getOrElse(sys.error(s"Not a case object: ${o.erasure}"))
        case c: ClassDescriptor => new ClassInstanceBuilder(c).result
      }
    }
  }

  private class CollectionBuilder(tpe: ScalaType) {
    def result: Any = {
      val randomMatch = RandomType(tpe.typeInfo, tpe.erasure, tpe.typeArgs)
      val custom = randomizers.randomizer(Databob.this)
      if (custom.isDefinedAt(randomMatch)) custom(randomMatch)
      else throw new RandomFailure("Expected collection but got " + tpe)
    }
  }

  private class ClassInstanceBuilder(descr: ClassDescriptor) {
    private def instantiate = {
      try {
        val constructor = descr.constructors.headOption.getOrElse(throw new RandomFailure("No constructor found for type " + descr.erasure))
        constructor.constructor.invoke(descr.companion, constructor.params.map(a => random(a.argType))).asInstanceOf[AnyRef]
      } catch {
        case e@(_: IllegalArgumentException | _: InstantiationException) => throw new RandomFailure("Could not construct class")
      }
    }

    def result: Any =
      customOrElse(descr.erasure) {
        case _ => instantiate
      }
  }

  private def customOrElse(target: ScalaType)(thunk: () => Any): Any = {
    val randomMatch = RandomType(target.typeInfo, target.erasure, target.typeArgs)
    val custom = randomizers.randomizer(this)
    if (custom.isDefinedAt(randomMatch)) {
      custom(randomMatch)
    } else thunk()
  }

  private def convert(target: ScalaType, r: Randomizers): Any = {
    val custom = r.randomizer(this)
    val randomMatch = RandomType(target.typeInfo, target.erasure, target.typeArgs)
    if (custom.isDefinedAt(randomMatch)) custom(randomMatch)
    else throw new RandomFailure("Do not know how to make a " + target.erasure)
  }
}

object Databob {
  def random[A](implicit randomizers: Randomizers = Randomizers(), mf: Manifest[A]): A = new Databob(randomizers).random[A]
}
