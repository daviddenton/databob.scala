package io.github.databob

import io.github.databob.generators.{ErasureGenerator, Generators, TypeGenerator}

trait Generator[A] {
  def mk(databob: Databob): PartialFunction[GeneratorType, A]
  def +(that: Generator[_]): Generators = this +: (that +: Generators.Empty)
}

object Generator {
  def apply[A: Manifest](mk: Databob => A): Generator[A] = new TypeGenerator[A](mk)
  def erasureBased[A: Manifest](mk: Databob => A): Generator[A] = new ErasureGenerator[A](mk)
  def typeBased[A: Manifest](mk: Databob => A): Generator[A] = new TypeGenerator[A](mk)
}