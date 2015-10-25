package io.github.databob.generators

import io.github.databob.Databob
import io.github.databob.Generator._

/**
 * Generators for Scala Collection types
 */
object ScalaCollectionGenerators {

  /**
   * Generates Empty Scala collections
   */
  lazy val Empty = erasureIsAssignableFrom[Map[_, _]]((gt, databob) => Map()) +
    erasureIsAssignableFrom[Set[_]]((gt, databob) => Set()) +
    erasureIsAssignableFrom[List[_]]((gt, databob) => List()) +
    erasureIsAssignableFrom[Vector[_]]((gt, databob) => Vector()) +
    erasureIsAssignableFrom[Seq[_]]((gt, databob) => Seq())

  /**
   * Generates Non-Empty Scala collections
   */
  lazy val NonEmpty = erasureIsAssignableFrom[Map[_, _]]((gt, databob) => Map(databob.mk(gt.typeArgs.head) -> databob.mk(gt.typeArgs(1)))) +
    erasureIsAssignableFrom[Set[_]]((gt, databob) => Set(databob.mk(gt.typeArgs.head))) +
    erasureIsAssignableFrom[List[_]]((gt, databob) => List(databob.mk(gt.typeArgs.head))) +
    erasureIsAssignableFrom[Vector[_]]((gt, databob) => Vector(databob.mk(gt.typeArgs.head))) +
    erasureIsAssignableFrom[Seq[_]]((gt, databob) => Seq(databob.mk(gt.typeArgs.head)))

  /**
   * Generates Random Scala collections
   */
  lazy val Random = erasureIsAssignableFrom[Map[_, _]]((gt, databob) => {
    if (Databob.random[Boolean]) Map()
    else {
      Map(randomRange.map(i => databob.mk(gt.typeArgs.head) -> databob.mk(gt.typeArgs(1))): _*)
    }
  }) +
    erasureIsAssignableFrom[Set[_]]((gt, databob) => {
      if (Databob.random[Boolean]) Set()
      else {
        Set(randomRange.map(i => databob.mk(gt.typeArgs.head)): _*)
      }
    }) +
    erasureIsAssignableFrom[List[_]]((gt, databob) => {
      if (Databob.random[Boolean]) List()
      else {
        List(randomRange.map(i => databob.mk(gt.typeArgs.head)): _*)
      }
    }) +
    erasureIsAssignableFrom[Vector[_]]((gt, databob) => {
      if (Databob.random[Boolean]) Vector()
      else {
        Vector(randomRange.map(i => databob.mk(gt.typeArgs.head)): _*)
      }
    }) +
    erasureIsAssignableFrom[Seq[_]]((gt, databob) => {
      if (Databob.random[Boolean]) Seq()
      else {
        Seq(randomRange.map(i => databob.mk(gt.typeArgs.head)): _*)
      }
    })

  private def randomRange: Range = {
    Range(0, util.Random.nextInt(5))
  }
}
