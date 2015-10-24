package io.github.databob.generators

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object MonadGenerators {

  val Happy = new Generators(
    List(
      new ErasureBasedGenerator[Try[_]]((gt, databob) => Success(databob.mk(gt.typeArgs.head))),
      new ErasureBasedGenerator[Future[_]]((gt, databob) => Future.successful(databob.mk(gt.typeArgs.head))),
      new ErasureBasedGenerator[Option[_]]((gt, databob) => Option(databob.mk(gt.typeArgs.head))),
      new ErasureBasedGenerator[Either[_, _]]((gt, databob) => Right(databob.mk(gt.typeArgs(1))))
    )
  )

  val Unhappy = new Generators(
    List(
      new ErasureBasedGenerator[Try[_]]((gt, databob) => Failure(databob.mk[Exception])),
      new ErasureBasedGenerator[Future[_]]((gt, databob) => Future.failed(databob.mk[Exception])),
      new ErasureBasedGenerator[Option[_]]((gt, databob) => None),
      new ErasureBasedGenerator[Either[_, _]]((gt, databob) => Left(databob.mk(gt.typeArgs.head)))
    )
  )

  val Random = Happy
}
