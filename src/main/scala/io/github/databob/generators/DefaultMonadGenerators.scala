package io.github.databob.generators

import io.github.databob.{Databob, Generator, GeneratorFailure, Generators}

import scala.util.control.Exception._

object DefaultMonadGenerators extends Generators(
  List(
    new Generator[Option[_]]() {
      override def mk(databob: Databob) = {
        case randomType if classOf[Option[_]].isAssignableFrom(randomType.erasure) => Option(databob.random(randomType.typeArgs.head))
      }
    },
    new Generator[Either[_, _]]() {
      override def mk(databob: Databob) = {
        case randomType if classOf[Either[_, _]].isAssignableFrom(randomType.erasure) => {
          (allCatch opt {
            Left(databob.random(randomType.typeArgs.head))
          } orElse (allCatch opt {
            Right(databob.random(randomType.typeArgs(1)))
          })).getOrElse(throw new GeneratorFailure("Expected value but got none"))
        }
      }
    }
  )
)
