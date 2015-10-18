package io.github.databob

import org.json4s.reflect.TypeInfo

trait Randomizer[A] {
  def newRandom(implicit r: Randomizers): PartialFunction[TypeInfo, A]
}

object Randomizer {
  private class CustomRandomizer[A: Manifest](mk: Randomizers => A) extends Randomizer[A] {

    val Class = implicitly[Manifest[A]].runtimeClass

    def newRandom(implicit r: Randomizers) = {
      case TypeInfo(Class, _) => mk(r)
    }
  }

  def apply[A: Manifest](mk: Randomizers => A): Randomizer[A] = new CustomRandomizer[A](mk)

}