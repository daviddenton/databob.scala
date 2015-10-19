package io.github.databob

import org.json4s.reflect.{ScalaType, TypeInfo}

case class RandomType(ti: TypeInfo, erasure: Class[_], typeArgs: Seq[ScalaType])
