package io.github.databob.generators

import io.github.databob.generators.ErasureBasedGenerator.erasureIsAssignableFrom

object CollectionGenerators {
  val Empty = new Generators(
    List(
      new ErasureBasedGenerator[java.util.ArrayList[_]](_.isArray, (gt, databob) => new java.util.ArrayList[Any]()),
      erasureIsAssignableFrom[java.util.ArrayList[_]](databob => new java.util.ArrayList[Any]()),
      erasureIsAssignableFrom[Map[_, _]](databob => Map()),
      erasureIsAssignableFrom[Set[_]](databob => Set()),
      erasureIsAssignableFrom[List[_]](databob => List()),
      erasureIsAssignableFrom[Seq[_]](databob => Seq())
    )
  )

  val Random = Empty
}
