package io.github.databob.generators

import io.github.databob.Databob
import io.github.databob.Generator._

import scala.collection.JavaConverters._

case class CollectionSize(value: Int)

/**
 * Generators for Collection types
 */
object CollectionGenerators {

  private def range(databob: Databob): Range = Range(0, databob.mk[CollectionSize].value)

  /**
   * Generates Empty collections
   */
  lazy val Empty = typeIs((databob) => CollectionSize(0)) +
    erasureIsAssignableFrom[Map[_, _]]((gt, databob) => Map(range(databob).map(i => databob.mk(gt.typeArgs.head) -> databob.mk(gt.typeArgs(1))): _*)) +
    erasureIsAssignableFrom[Set[_]]((gt, databob) => Set(range(databob).map(i => databob.mk(gt.typeArgs.head)): _*)) +
    erasureIsAssignableFrom[List[_]]((gt, databob) => List(range(databob).map(i => databob.mk(gt.typeArgs.head)): _*)) +
    erasureIsAssignableFrom[Vector[_]]((gt, databob) => Vector(range(databob).map(i => databob.mk(gt.typeArgs.head)): _*)) +
    erasureIsAssignableFrom[Seq[_]]((gt, databob) => Seq(range(databob).map(i => databob.mk(gt.typeArgs.head)): _*)) +
    new ErasureMatchingGenerator[Any](_.isArray, (gt, databob) => Array(range(databob).map(i => databob.mk(gt.typeArgs.head)): _*)) +
    erasureIsWithGen[java.util.List[_]]((gt, databob) => {
      val l = new java.util.ArrayList[Any]()
      l.addAll(range(databob).map(i => databob.mk(gt.typeArgs.head)).toList.asJava)
      l
    }) +
    erasureIsWithGen[java.util.Set[_]]((gt, databob) => {
      val s = new java.util.HashSet[Any]()
      s.addAll(range(databob).map(i => databob.mk(gt.typeArgs.head)).toList.asJava)
      s
    }) +
    erasureIsWithGen[java.util.Map[_, _]]((gt, databob) => {
      val map = Map(range(databob).map(i => databob.mk(gt.typeArgs.head) -> databob.mk(gt.typeArgs(1))): _*)
      val s = new java.util.HashMap[Any, Any]()
      s.putAll(map.asJava)
      s
    })

  /**
   * Generates Non-Empty collections
   */
  lazy val NonEmpty = typeIs((databob) => CollectionSize(1)) +: Empty

  /**
   * Generates Random collections
   */
  lazy val Random = typeIs((databob) => if (Databob.random[Boolean]) CollectionSize(scala.util.Random.nextInt(5)) else CollectionSize(0)) +: Empty
}
