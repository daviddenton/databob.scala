package io.github.databob

import java.time._

case class Randomizers(randomizers: List[Randomizer[_]] = DefaultRandomizers.randomizers) {

  implicit val RD = this

  def +(newRandomizer: Randomizer[_]): Randomizers = copy(randomizers = newRandomizer :: randomizers)

  def ++(that: Randomizers): Randomizers = copy(randomizers = that.randomizers ++ randomizers)

  def randomizer(databob: Databob) =
    randomizers.foldLeft(Map(): PartialFunction[RandomType, Any]) { (acc, x) =>
      acc.orElse(x.newRandom(databob))
    }
}

object DefaultRandomizers extends Randomizers(
  Java8DateTimeRandomizers.randomizers
)

object Java8DateTimeRandomizers extends Randomizers(
  List(
    Randomizer(databob => LocalDate.of(2000, 1, 1)),
    Randomizer(databob => LocalTime.of(0, 0, 0)),
    Randomizer(databob => LocalDateTime.of(databob.random[LocalDate], databob.random[LocalTime])),
    Randomizer(databob => ZonedDateTime.of(databob.random[LocalDateTime], ZoneId.systemDefault()))
  )
)

