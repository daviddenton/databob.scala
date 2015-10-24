package io.github.databob.generators

import io.github.databob.Generator.erasureIsAssignableFrom

object CollectionGenerators {
  val Empty = new Generators(
    List(
      new ErasureBasedGenerator[java.util.ArrayList[_]](_.isArray, (gt, databob) => new java.util.ArrayList[Any]()),
      erasureIsAssignableFrom[java.util.ArrayList[_]]((gt, databob) => new java.util.ArrayList[Any]()),
      erasureIsAssignableFrom[Map[_, _]]((gt, databob) => Map()),
      erasureIsAssignableFrom[Set[_]]((gt, databob) => Set()),
      erasureIsAssignableFrom[List[_]]((gt, databob) => List()),
      erasureIsAssignableFrom[Seq[_]]((gt, databob) => Seq())
    )
  )

  val Random = Empty
}
