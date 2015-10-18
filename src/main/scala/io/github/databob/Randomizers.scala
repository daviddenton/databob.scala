package io.github.databob

import java.time._

import org.json4s._

object Randomizers {
  private val defaultRandomizers = List[Randomizer[_]](
    Randomizer(databob => LocalDate.of(2000, 1, 1)),
    Randomizer(databob => LocalTime.of(0, 0, 0)),
    Randomizer(databob => LocalDateTime.of(databob.random[LocalDate], databob.random[LocalTime])),
    Randomizer(databob => ZonedDateTime.of(databob.random[LocalDateTime], ZoneId.systemDefault()))
  )
}

case class Randomizers(randomizers: List[Randomizer[_]] = Nil) {
  def +(newRandomizer: Randomizer[_]): Randomizers = copy(randomizers = newRandomizer :: randomizers)

  def randomizer(databob: Databob) =
    (randomizers ++ Randomizers.defaultRandomizers).foldLeft(Map(): PartialFunction[TypeInfo, Any]) { (acc, x) =>
      acc.orElse(x.newRandom(databob))
    }
}




