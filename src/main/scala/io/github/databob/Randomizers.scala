package io.github.databob

import java.lang.{Boolean => JavaBoolean, Byte => JavaByte, Double => JavaDouble, Float => JavaFloat, Integer => JavaInteger, Long => JavaLong, Short => JavaShort, String => JavaString}
import java.math.{BigDecimal => JavaBigDecimal, BigInteger => JavaBigInteger}
import java.sql.Timestamp
import java.time._
import java.util.Date

import io.github.databob.Randomizer.erasureBased

import scala.util.control.Exception._

case class Randomizers(randomizers: Iterable[Randomizer[_]] = Nil) extends Iterable[Randomizer[_]] {

  def +(newRandomizer: Randomizer[_]): Randomizers = copy(randomizers = newRandomizer :: randomizers.toList)

  def ++(that: Randomizers): Randomizers = copy(randomizers = that.randomizers ++ randomizers)

  def randomizer(databob: Databob) =
    randomizers.foldLeft(Map(): PartialFunction[RandomType, Any]) { (acc, x) =>
      acc.orElse(x.newRandom(databob))
    }

  override def iterator: Iterator[Randomizer[_]] = randomizers.iterator
}

object DefaultRandomizers extends Randomizers(
  JavaDateTimeRandomizers ++
    JavaPrimitiveRandomizers ++
    ScalaPrimitiveRandomizers ++
    CollectionRandomizers ++
    MonadRandomizers
)

object JavaDateTimeRandomizers extends Randomizers(
  List(
    Randomizer(databob => LocalDate.of(2000, 1, 1)),
    Randomizer(databob => LocalTime.of(0, 0, 0)),
    Randomizer(databob => LocalDateTime.of(databob.random[LocalDate], databob.random[LocalTime])),
    Randomizer(databob => ZonedDateTime.of(databob.random[LocalDateTime], ZoneId.systemDefault())),
    erasureBased(databob => new Date(0)),
    erasureBased(databob => new Timestamp(0))
  )
)

object JavaPrimitiveRandomizers extends Randomizers(
  List(
    erasureBased[JavaInteger](databob => new JavaInteger(0)),
    erasureBased[JavaLong](databob => new JavaLong(0)),
    erasureBased[JavaDouble](databob => new JavaDouble(0)),
    erasureBased[JavaBigDecimal](databob => BigDecimal(0).bigDecimal),
    erasureBased[JavaBigInteger](databob => BigInt(0).bigInteger),
    erasureBased[JavaFloat](databob => new JavaFloat(0)),
    erasureBased[JavaShort](databob => new JavaShort(0.toShort)),
    erasureBased[JavaByte](databob => new JavaByte(0.toByte)),
    erasureBased[JavaBoolean](databob => new JavaBoolean(false)),
    erasureBased[JavaString](databob => new JavaString(""))
  )
)

object ScalaPrimitiveRandomizers extends Randomizers(
  List(
    erasureBased[Int](databob => 0),
    erasureBased[Long](databob => 0L),
    erasureBased[Double](databob => 0.0d),
    erasureBased[BigDecimal](databob => BigDecimal(0)),
    erasureBased[BigInt](databob => BigInt(0)),
    erasureBased[Float](databob => 0.0f),
    erasureBased[Short](databob => 0),
    erasureBased[Byte](databob => 0.toByte),
    erasureBased[Boolean](databob => false),
    erasureBased[String](databob => "")
  )
)

object MonadRandomizers extends Randomizers(
  List(
    new Randomizer[Option[_]]() {
      override def newRandom(databob: Databob) = {
        case randomType if classOf[Option[_]].isAssignableFrom(randomType.erasure) => Option(databob.random(randomType.typeArgs.head))
      }
    },
    new Randomizer[Either[_, _]]() {
      override def newRandom(databob: Databob) = {
        case randomType if classOf[Either[_, _]].isAssignableFrom(randomType.erasure) => {
          (allCatch opt {
            Left(databob.random(randomType.typeArgs.head))
          } orElse (allCatch opt {
            Right(databob.random(randomType.typeArgs(1)))
          })).getOrElse(throw new RandomFailure("Expected value but got none"))
        }
      }
    }
  )
)

object CollectionRandomizers extends Randomizers(
  List(
    erasureBased[List[_]](databob => List()),
    erasureBased[Set[_]](databob => Set()),
    erasureBased[java.util.ArrayList[_]](databob => new java.util.ArrayList[Any]()),
    new Randomizer[Any]() {
      override def newRandom(databob: Databob) = {
        case randomType if randomType.erasure.isArray => java.lang.reflect.Array.newInstance(randomType.typeArgs.head.erasure, 0)
      }
    },
    new Randomizer[Map[_, _]]() {
      override def newRandom(databob: Databob) = {
        case randomType if classOf[collection.immutable.Map[_, _]].isAssignableFrom(randomType.erasure) ||
          classOf[collection.Map[_, _]].isAssignableFrom(randomType.erasure) => Map()
      }
    },
    new Randomizer[Seq[_]]() {
      override def newRandom(databob: Databob) = {
        case randomType if classOf[Seq[_]].isAssignableFrom(randomType.erasure) => Seq()
      }
    }
  )
)

