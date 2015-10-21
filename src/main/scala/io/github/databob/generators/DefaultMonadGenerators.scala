package io.github.databob.generators

import io.github.databob.{Databob, Generator, GeneratorFailure, Generators}

import scala.util.control.Exception._

object DefaultMonadGenerators extends Generators(
  List(
    new Generator[Option[_]]() {
      override def mk(databob: Databob) = {
        case generatorType if classOf[Option[_]].isAssignableFrom(generatorType.erasure) => Option(databob.mk(generatorType.typeArgs.head))
      }
    },
    new Generator[Either[_, _]]() {
      override def mk(databob: Databob) = {
        case generatorType if classOf[Either[_, _]].isAssignableFrom(generatorType.erasure) => {
          (allCatch opt {
            Left(databob.mk(generatorType.typeArgs.head))
          } orElse (allCatch opt {
            Right(databob.mk(generatorType.typeArgs(1)))
          })).getOrElse(throw new GeneratorFailure("Expected value but got none"))
        }
      }
    }
  )
)
