package io.github.databob

import org.json4s._

case class Randomizers(randomizers: List[Randomizer[_]] = Nil) {

  def +(newSerializer: Randomizer[_]): Randomizers = copy(randomizers = newSerializer :: randomizers)

  def randomizer(format: Randomizers) =
    randomizers.foldLeft(Map(): PartialFunction[TypeInfo, Any]) { (acc, x) =>
      acc.orElse(x.newRandom(format))
    }
}

trait Randomizer[A] {
  def newRandom(implicit format: Randomizers): PartialFunction[TypeInfo, A]
}

class CustomRandomizer[A: Manifest](ser: Randomizers => A) extends Randomizer[A] {

  val Class = implicitly[Manifest[A]].runtimeClass

  def newRandom(implicit format: Randomizers) = { case TypeInfo(Class, _) => ser(format) }
}
