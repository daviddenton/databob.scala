package io.github.databob.generators

import io.github.databob.Generator._

/**
 * Generators for Scala Primitive types
 */
object ScalaPrimitiveGenerators {

  /**
   * Creates Primitive values with their default values (0 for numeric, empty Strings, false)
   */
  val Defaults = erasureIs[Int](databob => 0) +
      erasureIs[Char](databob => 0.toChar) +
      erasureIs[Long](databob => 0L) +
      erasureIs[Double](databob => 0.0d) +
      erasureIs[BigDecimal](databob => BigDecimal(0)) +
      erasureIs[BigInt](databob => BigInt(0)) +
      erasureIs[Float](databob => 0.0f) +
      erasureIs[Short](databob => 0) +
      erasureIs[Byte](databob => 0.toByte) +
      erasureIs[Boolean](databob => false) +
      erasureIs[String](databob => "")

  /**
   * Creates random Primitive values
   */
  val Random = erasureIs[Int](databob => 0) +: Defaults
}
