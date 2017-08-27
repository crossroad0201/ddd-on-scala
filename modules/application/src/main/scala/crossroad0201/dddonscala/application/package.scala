package crossroad0201.dddonscala

import crossroad0201.dddonscala.domain.{DomainError, EntityId}

import scala.util.{Failure, Success, Try}

package object application {
  import scala.language.implicitConversions

  type ErrorCode = String

  // NOTE: アプリケーションサービスで、ドメインレイヤ／インフラレイヤ で発生するエラーを変換するための構文サポートです。

  implicit class DomainErrorOps[E <: DomainError, R](domainResult: Either[E, R]) {
    def ifLeftThen(f: E => ServiceError): Either[ServiceError, R] = {
      domainResult match {
        case Left(e)  => Left(f(e))
        case Right(r) => Right(r)
      }
    }
  }

  // FIXME インフラ層も、Try より　Either[Throwable, _] のほうがいいのでは？
  implicit class InfraErrorOps[S](infraResult: Try[S]) {
    def ifFailureThen(f: Throwable => ServiceError): Either[ServiceError, S] = {
      infraResult match {
        case Failure(e) => Left(f(e))
        case Success(s) => Right(s)
      }
    }
  }

  implicit class TryOptionOps[T](maybeValue: Try[Option[T]]) {
    def ifNotExists(f: => ServiceError)(): Either[ServiceError, T] = {
      maybeValue match {
        case Success(Some(s)) => Right(s)
        case Success(None)    => Left(f)
        case Failure(e)       => Left(SystemError(e))
      }
    }
  }

  def asServiceError[E](implicit f: E => ServiceError): E => ServiceError = f

}
