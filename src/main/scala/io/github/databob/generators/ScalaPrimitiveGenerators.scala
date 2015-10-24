package io.github.databob.generators

import io.github.databob.Generator._

object ScalaPrimitiveGenerators {
  val Default = new Generators(
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

  val Random = erasureBased[Int](databob => 0) +: Default
}
