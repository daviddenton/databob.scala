package io.github.databob

import java.time._

import org.json4s._

object Randomizers {
  private val defaultRandomizers = List[Randomizer[_]](
    new CustomRandomizer[LocalDate](r => LocalDate.of(2000, 1, 1)),
    new CustomRandomizer[LocalTime](r => LocalTime.of(0, 0, 0)),
    new CustomRandomizer[LocalDateTime](r => {
      LocalDateTime.of(LocalDate.of(2000, 1, 1), LocalTime.of(0, 0, 0))
    }),
    new CustomRandomizer[ZonedDateTime](r => {
      ZonedDateTime.of(LocalDateTime.of(LocalDate.of(2000, 1, 1), LocalTime.of(0, 0, 0)), ZoneId.systemDefault())
    })
  )
}
case class Randomizers(randomizers: List[Randomizer[_]] = Nil) {
  def +(newRandomizer: Randomizer[_]): Randomizers = copy(randomizers = newRandomizer :: randomizers)

  def randomizer(format: Randomizers) =
    (randomizers ++ Randomizers.defaultRandomizers).foldLeft(Map(): PartialFunction[TypeInfo, Any]) { (acc, x) =>
      acc.orElse(x.newRandom(format))
    }
}

trait Randomizer[A] {
  def newRandom(implicit format: Randomizers): PartialFunction[TypeInfo, A]
}

class CustomRandomizer[A: Manifest](r: Randomizers => A) extends Randomizer[A] {

  val Class = implicitly[Manifest[A]].runtimeClass

  def newRandom(implicit format: Randomizers) = {
    case TypeInfo(Class, _) => r(format)
  }
}
