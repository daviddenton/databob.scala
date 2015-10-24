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
    new ErasureMatchingGenerator[Any](_.isArray, (gt, databob) => Array()) +
      erasureIs[java.util.List[_]]((databob) => new java.util.ArrayList[Any]()) +
      erasureIs[java.util.Set[_]]((databob) => new java.util.HashSet[Any]()) +
      erasureIs[java.util.Map[_, _]]((databob) => new java.util.HashMap[Any, Any]())

  /**
   * Generates Non-Empty Java collections
   */
  val NonEmpty = new ErasureMatchingGenerator[Any](_.isArray, (gt, databob) => {
    Array(databob.mk(gt.typeArgs.head))
  }) +
    erasureIs2[java.util.List[_]]((gt, databob) => {
      val l = new java.util.ArrayList[Any]
      l.add(databob.mk(gt.typeArgs.head))
      l
    }) +
    erasureIs2[java.util.Set[_]]((gt, databob) => {
      val s = new java.util.HashSet[Any]
      s.add(databob.mk(gt.typeArgs.head))
      s
    }) +
    erasureIs2[java.util.Map[_, _]]((gt, databob) => {
      val s = new java.util.HashMap[Any, Any]()
      s.put(databob.mk(gt.typeArgs.head), databob.mk(gt.typeArgs(1)))
      s
    })

  /**
   * Generates Random Java collections
   */
  val Random = NonEmpty
}
