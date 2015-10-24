package io.github.databob.generators

import io.github.databob.Generator._
import io.github.databob.{Databob, Generator}

object CollectionGenerators {
  val Empty = new Generators(
    List(
      erasureBased[java.util.ArrayList[_]](databob => new java.util.ArrayList[Any]()),
      new Generator[Any]() {
        override def mk(databob: Databob) = {
          case generatorType if generatorType.erasure.isArray => java.lang.reflect.Array.newInstance(generatorType.typeArgs.head.erasure, 0)
        }
      },
      new ErasureBasedGenerator[Map[_, _]]((gt, databob) => Map()),
      new ErasureBasedGenerator[Set[_]]((gt, databob) => Set()),
      new ErasureBasedGenerator[List[_]]((gt, databob) => List()),
      new ErasureBasedGenerator[Seq[_]]((gt, databob) => Seq())
    )
  )

  val Random = Empty
}
