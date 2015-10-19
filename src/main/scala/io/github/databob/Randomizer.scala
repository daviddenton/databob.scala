package io.github.databob

import org.json4s.reflect.TypeInfo

trait Randomizer[A] {
  def newRandom(databob: Databob): PartialFunction[RandomType, A]
}

private class TypeRandomizer[A: Manifest](mk: Databob => A) extends Randomizer[A] {
  val Class = implicitly[Manifest[A]].runtimeClass

  def newRandom(databob: Databob): PartialFunction[RandomType, A] = {
    case RandomType(TypeInfo(Class, _), _) => mk(databob)
  }
}

private class ErasureRandomizer[A: Manifest](mk: Databob => A) extends Randomizer[A] {
  val Class = implicitly[Manifest[A]].runtimeClass

  def newRandom(databob: Databob): PartialFunction[RandomType, A] = {
    case RandomType(_, Class) => mk(databob)
  }
}

object Randomizer {
  def apply[A: Manifest](mk: Databob => A): Randomizer[A] = new TypeRandomizer[A](mk)
  def erasure[A: Manifest](mk: Databob => A): Randomizer[A] = new ErasureRandomizer[A](mk)
}