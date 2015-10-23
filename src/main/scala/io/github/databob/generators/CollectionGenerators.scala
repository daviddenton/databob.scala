package io.github.databob.generators

import io.github.databob.Generator._
import io.github.databob.{Databob, Generator, Generators}

object CollectionGenerators {
  val Empty = new Generators(
    List(
      erasureBased[java.util.ArrayList[_]](databob => new java.util.ArrayList[Any]()),
      new Generator[Any]() {
        override def mk(databob: Databob) = {
          case generatorType if generatorType.erasure.isArray => java.lang.reflect.Array.newInstance(generatorType.typeArgs.head.erasure, 0)
        }
      },
      new Generator[Map[_, _]]() {
        override def mk(databob: Databob) = {
          case generatorType if classOf[collection.immutable.Map[_, _]].isAssignableFrom(generatorType.erasure) ||
            classOf[collection.Map[_, _]].isAssignableFrom(generatorType.erasure) => Map()
        }
      },
      new Generator[Set[_]]() {
        override def mk(databob: Databob) = {
          case generatorType if classOf[Set[_]].isAssignableFrom(generatorType.erasure) => Set()
        }
      },
      new Generator[List[_]]() {
        override def mk(databob: Databob) = {
          case generatorType if classOf[List[_]].isAssignableFrom(generatorType.erasure) => List()
        }
      },
      new Generator[Seq[_]]() {
        override def mk(databob: Databob) = {
          case generatorType if classOf[Seq[_]].isAssignableFrom(generatorType.erasure) => Seq()
        }
      }
    )
  )
}
