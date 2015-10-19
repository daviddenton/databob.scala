package io.github.databob

import org.json4s.reflect.{ScalaType, TypeInfo}
import scala.language.existentials

case class RandomType(ti: TypeInfo, erasure: Class[_], typeArgs: Seq[ScalaType])
