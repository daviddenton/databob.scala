package io.github.databob

import io.github.databob.generators.{ErasureBasedGenerator, Generators, TypeGenerator}

/**
 * A generator for a particular type. Essentially a wrapped partial function which when matched will instantiate an A
 * @tparam A the type to be created if the partial function matches
 */
trait Generator[A] {
  /**
   * create the the partial function which will match the given type A and generate an instance of it
   * @param databob to use for generating dependant objects
   * @return a generated A instance
   */
  def pf(databob: Databob): PartialFunction[GeneratorType, A]

  /**
   * Combine this generator and with the passed generator, with this generator taking precedence
   * @param that the generator to append
   * @return the combined Generators
   */
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