package io.github.databob

import org.json4s.reflect.TypeInfo

trait Randomizer[A] {
  def newRandom(implicit format: Randomizers): PartialFunction[TypeInfo, A]
}

object Randomizer {
  private class CustomRandomizer[A: Manifest](r: Randomizers => A) extends Randomizer[A] {

    val Class = implicitly[Manifest[A]].runtimeClass

    def newRandom(implicit format: Randomizers) = {
      case TypeInfo(Class, _) => r(format)
    }
  }

  def apply[A: Manifest](r: Randomizers => A): Randomizer[A] = new CustomRandomizer[A](r)

}