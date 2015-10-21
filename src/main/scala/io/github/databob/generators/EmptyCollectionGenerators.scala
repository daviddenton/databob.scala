package io.github.databob.generators

import io.github.databob.Generator._
import io.github.databob.{Databob, Generator, Generators}

object EmptyCollectionGenerators extends Generators(
  List(
    erasureBased[java.util.ArrayList[_]](databob => new java.util.ArrayList[Any]()),
    new Generator[Any]() {
      override def mk(databob: Databob) = {
        case randomType if randomType.erasure.isArray => java.lang.reflect.Array.newInstance(randomType.typeArgs.head.erasure, 0)
      }
    },
    new Generator[Map[_, _]]() {
      override def mk(databob: Databob) = {
        case randomType if classOf[collection.immutable.Map[_, _]].isAssignableFrom(randomType.erasure) ||
          classOf[collection.Map[_, _]].isAssignableFrom(randomType.erasure) => Map()
      }
    },
    new Generator[Set[_]]() {
      override def mk(databob: Databob) = {
        case randomType if classOf[Set[_]].isAssignableFrom(randomType.erasure) => Set()
      }
    },
    new Generator[List[_]]() {
      override def mk(databob: Databob) = {
        case randomType if classOf[List[_]].isAssignableFrom(randomType.erasure) => List()
      }
    },
    new Generator[Seq[_]]() {
      override def mk(databob: Databob) = {
        case randomType if classOf[Seq[_]].isAssignableFrom(randomType.erasure) => Seq()
      }
    }
  )
)
