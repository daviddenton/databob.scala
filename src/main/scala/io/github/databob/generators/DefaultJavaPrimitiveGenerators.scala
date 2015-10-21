package io.github.databob.generators

import java.lang.{Boolean => JavaBoolean, Byte => JavaByte, Double => JavaDouble, Float => JavaFloat, Integer => JavaInteger, Long => JavaLong, Short => JavaShort, String => JavaString}
import java.math.{BigDecimal => JavaBigDecimal, BigInteger => JavaBigInteger}

import io.github.databob.Generator._
import io.github.databob.Generators


object DefaultJavaPrimitiveGenerators extends Generators(
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
