/**
 * Reflector logic taken from Json4S
 */
package io.github.databob

import java.lang.{Boolean => JavaBoolean, Byte => JavaByte, Double => JavaDouble, Float => JavaFloat, Integer => JavaInteger, Long => JavaLong, Short => JavaShort}
import java.math.{BigDecimal => JavaBigDecimal}
import java.sql.Timestamp
import java.util.Date

import org.json4s.reflect._

import scala.reflect.Manifest
import scala.util.control.Exception.allCatch

case class RandomFailure(msg: String) extends Exception(msg)

class Databob {

  def random[A](implicit randomizers: Randomizers = Randomizers(), mf: Manifest[A]): A = {
    try {
      random(Reflector.scalaTypeOf[A]).asInstanceOf[A]
    } catch {
      case e: RandomFailure => throw e
      case e: Exception =>
        throw new RandomFailure("unknown error" + e.getMessage)
    }
  }

  private def random(scalaType: ScalaType)(implicit randomizers: Randomizers): Any = {
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

  private class CollectionBuilder(tpe: ScalaType)(implicit randomizers: Randomizers) {
    def result: Any = {
      val custom = randomizers.randomizer(Databob.this)
      if (custom.isDefinedAt(tpe.typeInfo)) custom(tpe.typeInfo)
      else if (tpe.erasure == classOf[List[_]]) List()
      else if (tpe.erasure == classOf[Set[_]]) Set()
      else if (tpe.erasure == classOf[java.util.ArrayList[_]]) new java.util.ArrayList[Any]()
      else if (tpe.erasure.isArray) java.lang.reflect.Array.newInstance(tpe.typeArgs.head.erasure, 0)
      else if (classOf[Seq[_]].isAssignableFrom(tpe.erasure)) Seq()
      else throw new RandomFailure("Expected collection but got " + tpe)
    }
  }

  private class ClassInstanceBuilder(descr: ClassDescriptor)(implicit randomizers: Randomizers) {

    private[this] var _constructor: ConstructorDescriptor = null

    private[this] def constructor = {
      if (_constructor == null) {
        _constructor =
          if (descr.constructors.size == 1) descr.constructors.head
          else {
            val r = descr.bestMatching(Nil)
            r.getOrElse(throw new RandomFailure("No constructor for type " + descr.erasure))
          }
      }
      _constructor
    }

    private[this] def instantiate = {
      try {
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

  private[this] def customOrElse(target: ScalaType)(thunk: () => Any)(implicit randomizers: Randomizers): Any = {
    val custom = randomizers.randomizer(this)
    if (custom.isDefinedAt(target.typeInfo)) {
      custom(target.typeInfo)
    } else thunk()
  }

  private[this] def convert(target: ScalaType, r: Randomizers): Any = {
    if (target.erasure == classOf[Int]) 0
    else if (target.erasure == classOf[JavaInteger]) new JavaInteger(0)
    else if (target.erasure == classOf[BigInt]) 0
    else if (target.erasure == classOf[Long]) 0L
    else if (target.erasure == classOf[JavaLong]) new JavaLong(0L)
    else if (target.erasure == classOf[Double]) 0.0d
    else if (target.erasure == classOf[JavaDouble]) new JavaDouble(0.0d)
    else if (target.erasure == classOf[BigDecimal]) BigDecimal(0)
    else if (target.erasure == classOf[JavaBigDecimal]) BigDecimal(0).bigDecimal
    else if (target.erasure == classOf[Float]) 0.0f
    else if (target.erasure == classOf[JavaFloat]) new JavaFloat(0.0f)
    else if (target.erasure == classOf[Short]) 0
    else if (target.erasure == classOf[JavaShort]) new JavaShort(0.shortValue)
    else if (target.erasure == classOf[Byte]) 0.byteValue
    else if (target.erasure == classOf[JavaByte]) new JavaByte(0.byteValue)
    else if (target.erasure == classOf[Boolean]) false
    else if (target.erasure == classOf[JavaBoolean]) new JavaBoolean(false)
    else if (target.erasure == classOf[String]) ""
    else if (target.erasure == classOf[Number]) 0L
    else if (target.erasure == classOf[Date]) new Date(0)
    else if (target.erasure == classOf[Timestamp]) new Timestamp(0)
    else {
      val custom = r.randomizer(this)
      if (custom.isDefinedAt(target.typeInfo)) custom(target.typeInfo)
      else throw new RandomFailure("Do not know how to make a " + target.erasure)
    }
  }
}

object Databob extends Databob
