package crossroad0201.dddonscala

import crossroad0201.dddonscala.domain.DomainError
import crossroad0201.dddonscala.infrastructure.rdb.OptimisticLockException

import scala.util.{Failure, Success, Try}

package object applications {
  import scala.language.implicitConversions

  type ErrorCode = String

  implicit class DomainErrorOps[E <: DomainError, R](domainResult: Either[E, R]) {
    def ifLeftThen(f: E => ApplicationError): Either[ApplicationError, R] = {
      domainResult match {
        case Left(e)  => Left(f(e))
        case Right(r) => Right(r)
      }
    }
  }

  implicit class InfraErrorOps[S](infraResult: Try[S]) {
    def ifFailureThen(f: Throwable => ApplicationError): Either[ApplicationError, S] = {
      infraResult match {
        case Failure(e) => Left(f(e))
        case Success(s) => Right(s)
      }
    }
  }

  implicit class TryOptionOps[T](maybeValue: Try[Option[T]]) {
    def ifNotExists(f: => ApplicationError): Either[ApplicationError, T] = {
      maybeValue match {
        case Success(Some(s)) => Right(s)
        case Success(None)    => Left(f)
        case Failure(e)       => Left(defaultThrowableHandler(e))
      }
    }
  }

  def applicationError[E](implicit f: E => ApplicationError): E => ApplicationError = f

  implicit val defaultThrowableHandler: Throwable => ApplicationError = {
    case e: OptimisticLockException => ConflictError(e)
    case e => SystemError(e)
  }

}
