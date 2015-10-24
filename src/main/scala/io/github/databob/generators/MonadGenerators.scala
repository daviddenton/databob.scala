package io.github.databob.generators

import io.github.databob.Generator._

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
 * Generators for Monadic Scala types
 */
object MonadGenerators {

  /**
   * Generates 'happy' monad values (Some Option, Right Either etc)
   */
  val Happy = erasureIsAssignableFrom[Try[_]]((gt, databob) => Success(databob.mk(gt.typeArgs.head))) +
    erasureIsAssignableFrom[Future[_]]((gt, databob) => Future.successful(databob.mk(gt.typeArgs.head))) +
    erasureIsAssignableFrom[Option[_]]((gt, databob) => Option(databob.mk(gt.typeArgs.head))) +
    erasureIsAssignableFrom[Either[_, _]]((gt, databob) => Right(databob.mk(gt.typeArgs(1))))

  /**
   * Generates 'unhappy' monad values (None Option, Left Either etc)
   */
  val Unhappy = erasureIsAssignableFrom[Try[_]]((gt, databob) => Failure(databob.mk[Exception])) +
    erasureIsAssignableFrom[Future[_]]((gt, databob) => Future.failed(databob.mk[Exception])) +
    erasureIsAssignableFrom[Option[_]]((gt, databob) => None) +
    erasureIsAssignableFrom[Either[_, _]]((gt, databob) => Left(databob.mk(gt.typeArgs.head)))

  /**
   * Generates Random monad values
   */
  val Random = Happy
}
