package io.github.databob

import java.time._

import org.json4s._

case class Randomizers(randomizers: List[Randomizer[_]] = Nil) {

  implicit val RD = this
  private val defaultRandomizers = List[Randomizer[_]](
    Randomizer(databob => LocalDate.of(2000, 1, 1)),
    Randomizer(databob => LocalTime.of(0, 0, 0)),
    Randomizer(databob => LocalDateTime.of(databob.random[LocalDate], databob.random[LocalTime])),
    Randomizer(databob => ZonedDateTime.of(databob.random[LocalDateTime], ZoneId.systemDefault()))
  )

  def +(newRandomizer: Randomizer[_]): Randomizers = copy(randomizers = newRandomizer :: randomizers)

  def randomizer(databob: Databob) =
    (randomizers ++ defaultRandomizers).foldLeft(Map(): PartialFunction[TypeInfo, Any]) { (acc, x) =>
      acc.orElse(x.newRandom(databob))
    }
}




