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
   * Generates Non-Empty Scala collections
   */
  val NonEmpty = erasureIsAssignableFrom[Map[_, _]]((gt, databob) => Map(databob.mk(gt.typeArgs.head) -> databob.mk(gt.typeArgs(1)))) +
    erasureIsAssignableFrom[Set[_]]((gt, databob) => Set(databob.mk(gt.typeArgs.head))) +
    erasureIsAssignableFrom[List[_]]((gt, databob) => List(databob.mk(gt.typeArgs.head))) +
    erasureIsAssignableFrom[Vector[_]]((gt, databob) => Vector(databob.mk(gt.typeArgs.head))) +
    erasureIsAssignableFrom[Seq[_]]((gt, databob) => Seq(databob.mk(gt.typeArgs.head)))

  /**
   * Generates Random Scala collections
   */
  val Random = NonEmpty
}
