package io.github.databob.generators

import io.github.databob.{Databob, Generator, GeneratorType}

class ErasureBasedGenerator[A: Manifest](fn: (GeneratorType, Databob) => A) extends Generator[A]() {
  val Class = implicitly[Manifest[A]].runtimeClass

  override def mk(databob: Databob) = {
    case generatorType if Class.isAssignableFrom(generatorType.erasure) => fn(generatorType, databob)
  }
}
