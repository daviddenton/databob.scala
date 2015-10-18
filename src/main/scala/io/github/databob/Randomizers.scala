package io.github.databob

import java.time._

import org.json4s._

object Randomizers {
  private val defaultRandomizers = List[Randomizer[_]](
    Randomizer(r => LocalDate.of(2000, 1, 1)),
    Randomizer(r => LocalTime.of(0, 0, 0)),
    Randomizer(r => {
      r
      LocalDateTime.of(LocalDate.of(2000, 1, 1), LocalTime.of(0, 0, 0))
    }),
    Randomizer(r => {
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




