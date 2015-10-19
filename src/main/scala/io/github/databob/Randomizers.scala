package io.github.databob

import java.lang.{Boolean => JavaBoolean, Byte => JavaByte, Double => JavaDouble, Float => JavaFloat, Integer => JavaInteger, Long => JavaLong, Short => JavaShort, String => JavaString}
import java.math.{BigDecimal => JavaBigDecimal, BigInteger}
import java.sql.Timestamp
import java.time._
import java.util.Date

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
  JavaDateTimeRandomizers.randomizers ++
    JavaPrimitiveRandomizers.randomizers ++
    ScalaPrimitiveRandomizers.randomizers
)

object JavaDateTimeRandomizers extends Randomizers(
  List(
    Randomizer.apply(databob => LocalDate.of(2000, 1, 1)),
    Randomizer.apply(databob => LocalTime.of(0, 0, 0)),
    Randomizer.apply(databob => LocalDateTime.of(databob.random[LocalDate], databob.random[LocalTime])),
    Randomizer.apply(databob => ZonedDateTime.of(databob.random[LocalDateTime], ZoneId.systemDefault())),
    Randomizer.erasure(databob => new Date(0)),
    Randomizer.erasure(databob => new Timestamp(0))
  )
)

object JavaPrimitiveRandomizers extends Randomizers(
  List(
    Randomizer.erasure[JavaInteger](databob => new JavaInteger(0)),
    Randomizer.erasure[JavaLong](databob => new JavaLong(0)),
    Randomizer.erasure[JavaDouble](databob => new JavaDouble(0)),
    Randomizer.erasure[JavaBigDecimal](databob => BigDecimal(0).bigDecimal),
    Randomizer.erasure[BigInteger](databob => BigInt(0).bigInteger),
    Randomizer.erasure[JavaFloat](databob => new JavaFloat(0)),
    Randomizer.erasure[JavaShort](databob => new JavaShort(0.toShort)),
    Randomizer.erasure[JavaByte](databob => new JavaByte(0.toByte)),
    Randomizer.erasure[JavaBoolean](databob => new JavaBoolean(false)),
    Randomizer.erasure[JavaString](databob => new JavaString(""))
  )
)

object ScalaPrimitiveRandomizers extends Randomizers(
  List(
    Randomizer.erasure[Int](databob => 0),
    Randomizer.erasure[Long](databob => 0L),
    Randomizer.erasure[Double](databob => 0.0d),
    Randomizer.erasure[BigDecimal](databob => BigDecimal(0)),
    Randomizer.erasure[BigInt](databob => BigInt(0)),
    Randomizer.erasure[Float](databob => 0.0f),
    Randomizer.erasure[Short](databob => 0),
    Randomizer.erasure[Byte](databob => 0.toByte),
    Randomizer.erasure[Boolean](databob => false),
    Randomizer.erasure[String](databob => "")
  )
)

