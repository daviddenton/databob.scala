package io.github.databob

import io.github.databob.generators.{ErasureBasedGenerator, Generators, TypeGenerator}

trait Generator[A] {
  def mk(databob: Databob): PartialFunction[GeneratorType, A]
  def +(that: Generator[_]): Generators = this +: (that +: Generators.Empty)
}

object Generator {
  def apply[A: Manifest](mk: Databob => A): Generator[A] = new TypeGenerator[A](mk)
  def typeIs[A: Manifest](mk: Databob => A): Generator[A] = new TypeGenerator[A](mk)

  def erasureIsAssignableFrom[R: Manifest](fn: (GeneratorType, Databob) => R) =
    new ErasureBasedGenerator(_.isAssignableFrom(implicitly[Manifest[R]].runtimeClass), fn)

  def erasureIs[R: Manifest](fn: Databob => R) =
    new ErasureBasedGenerator(_ == implicitly[Manifest[R]].runtimeClass, (gt, databob) => fn(databob))

}