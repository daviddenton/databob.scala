package io.github.databob.generators

import io.github.databob.Generator._

/**
 * Generators for Java Collection types
 */
object JavaCollectionGenerators {

  /**
   * Generates Empty Java collections
   */
  val Empty =
    new ErasureBasedGenerator[Any](_.isArray, (gt, databob) => java.lang.reflect.Array.newInstance(gt.typeArgs.head.erasure, 0)) +
      erasureIs[java.util.List[_]]((databob) => new java.util.ArrayList[Any]()) +
      erasureIs[java.util.Set[_]]((databob) => new java.util.HashSet[Any]()) +
      erasureIs[java.util.Map[_, _]]((databob) => new java.util.HashMap[Any, Any]())

  /**
   * Generates Random Java collections
   */
  val Random = Empty
}
