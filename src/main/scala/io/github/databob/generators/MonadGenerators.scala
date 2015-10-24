package io.github.databob.generators

import io.github.databob.generators.ErasureBasedGenerator._

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object MonadGenerators {

  val Happy = new Generators(
    List(
      erasureIsAssignableFrom2[Try[_]]((gt, databob) => Success(databob.mk(gt.typeArgs.head))),
      erasureIsAssignableFrom2[Future[_]]((gt, databob) => Future.successful(databob.mk(gt.typeArgs.head))),
      erasureIsAssignableFrom2[Option[_]]((gt, databob) => Option(databob.mk(gt.typeArgs.head))),
      erasureIsAssignableFrom2[Either[_, _]]((gt, databob) => Right(databob.mk(gt.typeArgs(1))))
    )
  )

  val Unhappy = new Generators(
    List(
      erasureIsAssignableFrom[Try[_]](databob => Failure(databob.mk[Exception])),
      erasureIsAssignableFrom[Future[_]](databob => Future.failed(databob.mk[Exception])),
      erasureIsAssignableFrom[Option[_]](databob => None),
      erasureIsAssignableFrom2[Either[_, _]]((gt, databob) => Left(databob.mk(gt.typeArgs.head)))
    )
  )

  val Random = Happy
}
