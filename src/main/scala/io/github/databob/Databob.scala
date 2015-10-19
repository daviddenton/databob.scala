/**
 * Reflector logic taken from Json4S
 */
package io.github.databob

import org.json4s.reflect._

import scala.reflect.Manifest

case class RandomFailure(msg: String) extends Exception(msg)

class Databob(randomizers: Randomizers = new Randomizers()) {

  def random[A](implicit mf: Manifest[A]): A = {
    try {
      random(Reflector.scalaTypeOf[A]).asInstanceOf[A]
    } catch {
      case e: RandomFailure => throw e
      case e: Exception =>
        throw new RandomFailure("unknown error" + e.getMessage)
    }
  }

  private[databob] def random(scalaType: ScalaType): Any = {
    val randomType = RandomType(scalaType.typeInfo, scalaType.erasure, scalaType.typeArgs)
    val r = randomizers.randomizer(this)

    if (r.isDefinedAt(randomType)) r(randomType)
    else {
      Reflector.describe(scalaType) match {
        case o: ClassDescriptor if o.erasure.isSingleton =>
          o.erasure.singletonInstance.getOrElse(sys.error(s"Not r case object: ${o.erasure}"))
        case c: ClassDescriptor => new ClassInstanceBuilder(c).result
      }
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

    def result: Any = {
      val target = descr.erasure
      val randomMatch = RandomType(target.typeInfo, target.erasure, target.typeArgs)
      val custom = randomizers.randomizer(Databob.this)
      if (custom.isDefinedAt(randomMatch)) custom(randomMatch) else instantiate
    }
  }
}

object Databob {
  def random[A](implicit randomizers: Randomizers = DefaultRandomizers, mf: Manifest[A]): A = new Databob(randomizers).random[A]
}
