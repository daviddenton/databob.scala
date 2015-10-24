package io.github.databob.generators

import io.github.databob.{Databob, Generator, GeneratorType}

class ErasureBasedGenerator[A: Manifest](predicate: (Class[_]) => Boolean, fn: (GeneratorType, Databob) => A) extends Generator[A]() {
  override def mk(databob: Databob) = {
    case generatorType if predicate(generatorType.erasure) => fn(generatorType, databob)
  }
}
