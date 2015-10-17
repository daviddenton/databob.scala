package io.github.daviddenton.databob

import scala.reflect._
import scala.reflect.runtime.universe._
import scala.reflect.runtime.{currentMirror => cm}

case class Age(age: Int)
case class Person(name: String, age: Age)

object Test extends App {

  def of[A](a: A)(implicit t: ClassTag[A]): ClassTag[A] = t

  def random[A]()(implicit t: ClassTag[A]): A = {
    val claass = cm classSymbol t.runtimeClass
    val moduule = claass.companion.asModule
    val im = cm reflect (cm reflectModule moduule).instance
    default[A](im, "apply")
  }
  import scala.reflect.runtime.{universe => ru}

  def default[A](im: InstanceMirror, name: String): A = {
    val at = TermName(name)
    val ts = im.symbol.typeSignature
    val method = (ts member at).asMethod

    def valueFor(p: Symbol, i: Int): Any = {
      val defarg = ts member TermName(s"$name$$ default$$${i + 1}")
      if (defarg != NoSymbol) {
        (im reflectMethod defarg.asMethod)()
      } else {
        p.typeSignature match {
          case t if t =:= typeOf[String] => "hello"
          case t if t =:= typeOf[Int] => 0
          case x => {
            val universeMirror = ru.runtimeMirror(getClass.getClassLoader)

            def companionOf[T](implicit tt: ru.TypeTag[T])  = {
              val companionMirror = universeMirror.reflectModule(ru.typeOf[T].typeSymbol.companionSymbol.asModule)
              companionMirror.instance
            }
            random()(of(x))
          }
        }
      }
    }
    val args = (for (ps <- method.paramLists; p <- ps) yield p).zipWithIndex map (p => valueFor(p._1, p._2))
    (im reflectMethod method)(args: _*).asInstanceOf[A]
  }

  assert(Person("hello", Age(12)) == random[Person]())
}