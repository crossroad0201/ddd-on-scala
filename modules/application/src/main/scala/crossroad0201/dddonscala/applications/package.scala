package crossroad0201.dddonscala

import crossroad0201.dddonscala.domain.DomainError

import scala.util.{ Failure, Success, Try }

package object applications {
  import scala.language.implicitConversions

  implicit def asApplicationError[R](domainResult: Either[DomainError, R]): Either[ApplicationError, R] =
    domainResult match {
      case Right(r) => Right(r)
      case Left(l)  => Left(new ApplicationError {})
    }

  implicit def asApplicationError[S](infraResult: Try[S]): Either[ApplicationError, S] =
    infraResult match {
      case Success(s) => Right(s)
      case Failure(e) => Left(new ApplicationError {})
    }

  def shouldExists[T](maybeExists: Option[T]): Either[DomainError, T] =
    maybeExists match {
      case Some(s) => Right(s)
      case None    => Left(new DomainError {})
    }

}
