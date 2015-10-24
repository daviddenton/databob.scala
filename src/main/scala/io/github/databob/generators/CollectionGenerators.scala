package io.github.databob.generators

import io.github.databob.Generator.erasureIsAssignableFrom

/**
 * Generators for Collection types
 */
object CollectionGenerators {

  /**
   * Generates Empty collections
   */
  val Empty = new ErasureBasedGenerator[java.util.ArrayList[_]](_.isArray, (gt, databob) => new java.util.ArrayList[Any]()) +
    erasureIsAssignableFrom[Map[_, _]]((gt, databob) => Map()) +
    erasureIsAssignableFrom[Set[_]]((gt, databob) => Set()) +
    erasureIsAssignableFrom[List[_]]((gt, databob) => List()) +
    erasureIsAssignableFrom[Seq[_]]((gt, databob) => Seq())

  /**
   * Generates Random collections
   */
  val Random = Empty
}
