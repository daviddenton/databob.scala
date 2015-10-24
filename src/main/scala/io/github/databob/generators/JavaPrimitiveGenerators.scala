package io.github.databob.generators

import java.lang.{Boolean => JavaBoolean, Byte => JavaByte, Character => JavaCharacter, Double => JavaDouble, Float => JavaFloat, Integer => JavaInteger, Long => JavaLong, Short => JavaShort, String => JavaString}
import java.math.{BigDecimal => JavaBigDecimal, BigInteger => JavaBigInteger}

import io.github.databob.Generator.erasureIs

/**
 * Generators for Java Primitive types
 */
object JavaPrimitiveGenerators {

  /**
   * Creates Primitive values with their default values (0 for numeric, empty Strings, false)
   */
  val Defaults = erasureIs[JavaInteger](databob => new JavaInteger(0)) +
    erasureIs[JavaLong](databob => new JavaLong(0)) +
    erasureIs[JavaDouble](databob => new JavaDouble(0)) +
    erasureIs[JavaBigDecimal](databob => BigDecimal(0).bigDecimal) +
    erasureIs[JavaBigInteger](databob => BigInt(0).bigInteger) +
    erasureIs[JavaFloat](databob => new JavaFloat(0)) +
    erasureIs[JavaShort](databob => new JavaShort(0.toShort)) +
    erasureIs[JavaByte](databob => new JavaByte(0.toByte)) +
    erasureIs[JavaBoolean](databob => new JavaBoolean(false)) +
    erasureIs[JavaCharacter](databob => new JavaCharacter(0.toChar)) +
    erasureIs[JavaString](databob => new JavaString(""))

  /**
   * Creates random Primitive values
   */
  val Random = erasureIs[JavaInteger](databob => new JavaInteger(0)) +: Defaults
}
