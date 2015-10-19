package io.github.databob

import org.json4s.reflect.TypeInfo

case class RandomType(ti: TypeInfo, erasure: Class[_])
