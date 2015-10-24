package io.github.databob.generators

import io.github.databob.Generator._

/**
 * Generators for Scala Collection types
 */
object ScalaCollectionGenerators {

  /**
   * Generates Empty Scala collections
   */
  val Empty = erasureIsAssignableFrom[Map[_, _]]((gt, databob) => Map()) +
    erasureIsAssignableFrom[Set[_]]((gt, databob) => Set()) +
    erasureIsAssignableFrom[List[_]]((gt, databob) => List()) +
    erasureIsAssignableFrom[Vector[_]]((gt, databob) => Vector()) +
    erasureIsAssignableFrom[Seq[_]]((gt, databob) => Seq())

  /**
   * Generates Random Scala collections
   */
  val Random = Empty
}
