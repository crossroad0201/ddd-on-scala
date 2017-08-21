package crossroad0201.dddonscala

import crossroad0201.dddonscala.domain.DomainError

import scala.util.{Failure, Success, Try}

package object applications {
  import scala.language.implicitConversions

  // FIXME xxxx.asApplicationError のほうがいいかもしれない
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

  def shouldExists[T](maybeExists: Try[Option[T]]): Either[ApplicationError, T] =
    maybeExists match {
      case Success(Some(s)) => Right(s)
      case Success(None)    => Left(new ApplicationError {})
      case Failure(e)       => Left(new ApplicationError {})
    }

}
