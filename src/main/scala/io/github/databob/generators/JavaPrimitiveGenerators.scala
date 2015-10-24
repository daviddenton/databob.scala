package io.github.databob.generators

import java.lang.{Boolean => JavaBoolean, Byte => JavaByte, Double => JavaDouble, Float => JavaFloat, Integer => JavaInteger, Long => JavaLong, Short => JavaShort, String => JavaString}
import java.math.{BigDecimal => JavaBigDecimal, BigInteger => JavaBigInteger}

import io.github.databob.Generator.erasureIs

object JavaPrimitiveGenerators {
  val Defaults = new Generators(
    List(
      erasureIs[JavaInteger](databob => new JavaInteger(0)),
      erasureIs[JavaLong](databob => new JavaLong(0)),
      erasureIs[JavaDouble](databob => new JavaDouble(0)),
      erasureIs[JavaBigDecimal](databob => BigDecimal(0).bigDecimal),
      erasureIs[JavaBigInteger](databob => BigInt(0).bigInteger),
      erasureIs[JavaFloat](databob => new JavaFloat(0)),
      erasureIs[JavaShort](databob => new JavaShort(0.toShort)),
      erasureIs[JavaByte](databob => new JavaByte(0.toByte)),
      erasureIs[JavaBoolean](databob => new JavaBoolean(false)),
      erasureIs[JavaString](databob => new JavaString(""))
    )
  )

  val Random = erasureIs[JavaInteger](databob => new JavaInteger(0)) +: Defaults
}
