package io.github.databob.generators

import java.lang.{Boolean => JavaBoolean, Byte => JavaByte, Character => JavaCharacter, Double => JavaDouble, Float => JavaFloat, Long => JavaLong, Short => JavaShort}

import io.github.databob.Generator._

/**
 * Generators for Primitive types
 */
object PrimitiveGenerators {

  /**
   * Creates Primitive values with their default values (0 for numeric, empty Strings, false)
   */
  lazy val Defaults = erasureIs[Int](databob => 0) +
    erasureIs[Char](databob => 0.toChar) +
    erasureIs[Long](databob => 0L) +
    erasureIs[Double](databob => 0.0d) +
    erasureIs[BigDecimal](databob => BigDecimal(0)) +
    erasureIs[BigInt](databob => BigInt(0)) +
    erasureIs[String](databob => "") +
    erasureIs[Float](databob => 0.0f) +
    erasureIs[Short](databob => 0) +
    erasureIs[Byte](databob => 0.toByte) +
    erasureIs[Boolean](databob => false) +
    erasureIs[JavaLong](databob => new JavaLong(0)) +
    erasureIs[JavaDouble](databob => new JavaDouble(0)) +
    erasureIs[JavaFloat](databob => new JavaFloat(0)) +
    erasureIs[JavaShort](databob => new JavaShort(0.toShort)) +
    erasureIs[JavaByte](databob => new JavaByte(0.toByte)) +
    erasureIs[JavaBoolean](databob => new JavaBoolean(false)) +
    erasureIs[JavaCharacter](databob => new JavaCharacter(0.toChar))

  /**
   * Creates random Primitive values
   */
  lazy val Random = erasureIs[Int](databob => 0) +: Defaults
}
