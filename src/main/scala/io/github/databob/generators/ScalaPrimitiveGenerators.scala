package io.github.databob.generators

import io.github.databob.Generator._

object ScalaPrimitiveGenerators {
  val Defaults = new Generators(
    List(
      erasureIs[Int](databob => 0),
      erasureIs[Long](databob => 0L),
      erasureIs[Double](databob => 0.0d),
      erasureIs[BigDecimal](databob => BigDecimal(0)),
      erasureIs[BigInt](databob => BigInt(0)),
      erasureIs[Float](databob => 0.0f),
      erasureIs[Short](databob => 0),
      erasureIs[Byte](databob => 0.toByte),
      erasureIs[Boolean](databob => false),
      erasureIs[String](databob => "")
    )
  )

  val Random = erasureIs[Int](databob => 0) +: Defaults
}
