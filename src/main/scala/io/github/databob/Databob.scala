package io.github.databob

import io.github.databob.generators.DefaultGenerators
import org.json4s.reflect.Reflector._
import org.json4s.reflect._

import scala.reflect.Manifest



class Databob(randomizers: Generators = new Generators()) {

  def random[A](implicit mf: Manifest[A]): A = {
    try {
      random(scalaTypeOf[A]).asInstanceOf[A]
    } catch {
      case e: GeneratorFailure => throw e
      case e: Exception => throw new GeneratorFailure("unknown error" + e.getMessage)
    }
  }

  private[databob] def random(scalaType: ScalaType): Any = {
    val randomType = GeneratorType(scalaType.typeInfo, scalaType.erasure, scalaType.typeArgs)
    val r = randomizers.pf(this)

    if (r.isDefinedAt(randomType)) r(randomType)
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
        constructor.constructor.invoke(descr.companion, constructor.params.map(a => random(a.argType))).asInstanceOf[AnyRef]
      } catch {
        case e@(_: IllegalArgumentException | _: InstantiationException) => throw new GeneratorFailure("Could not construct class")
      }
    }

    def result: Any = {
      val target = descr.erasure
      val randomType = GeneratorType(target.typeInfo, target.erasure, target.typeArgs)
      val r = randomizers.pf(Databob.this)
      if (r.isDefinedAt(randomType)) r(randomType) else instantiate
    }
  }
}

object Databob {
  def random[A](implicit randomizers: Generators = DefaultGenerators, mf: Manifest[A]): A = new Databob(randomizers).random[A]
}
