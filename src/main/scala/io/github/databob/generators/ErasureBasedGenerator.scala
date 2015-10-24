package io.github.databob.generators

import io.github.databob.{Databob, Generator, GeneratorType}

class ErasureBasedGenerator[A: Manifest](predicate: (Class[_]) => Boolean, fn: (GeneratorType, Databob) => A) extends Generator[A]() {
  override def mk(databob: Databob) = {
    case generatorType if predicate(generatorType.erasure) => fn(generatorType, databob)
  }
}

object ErasureBasedGenerator {
  def erasureIsAssignableFrom2[R: Manifest](fn: (GeneratorType, Databob) => R) =
    new ErasureBasedGenerator(_.isAssignableFrom(implicitly[Manifest[R]].runtimeClass), fn)

  def erasureIsAssignableFrom[R: Manifest](fn: Databob => R) =
    new ErasureBasedGenerator(_.isAssignableFrom(implicitly[Manifest[R]].runtimeClass), (gt, databob) => fn(databob))

  def erasureIs2[R: Manifest](fn: (GeneratorType, Databob) => R) =
    new ErasureBasedGenerator(_ == implicitly[Manifest[R]].runtimeClass, fn)

  def erasureIs[R: Manifest](fn: Databob => R) =
    new ErasureBasedGenerator(_ == implicitly[Manifest[R]].runtimeClass, (gt, databob) => fn(databob))
}