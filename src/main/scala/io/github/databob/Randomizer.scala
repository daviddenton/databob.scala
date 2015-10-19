package io.github.databob

import org.json4s.reflect.TypeInfo

trait Randomizer[A] {
  def newRandom(databob: Databob): PartialFunction[RandomType, A]
}

object Randomizer {
  private class CustomRandomizer[A: Manifest](mk: Databob => A) extends Randomizer[A] {

    val Class = implicitly[Manifest[A]].runtimeClass

    def newRandom(databob: Databob): PartialFunction[RandomType, A] = {
      case RandomType(TypeInfo(Class, _), _) => mk(databob)
    }
  }

  def apply[A: Manifest](mk: Databob => A): Randomizer[A] = new CustomRandomizer[A](mk)

}