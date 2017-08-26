package crossroad0201.dddonscala.adapter.controller.sample

import crossroad0201.dddonscala.application.{ConflictedError, ServiceError, SystemError, TransactionAware}
import crossroad0201.dddonscala.domain.{EntityIdGenerator, EntityMetaDataCreator, UnitOfWork}
import crossroad0201.dddonscala.infrastructure.rdb.{OptimisticLockException, ScalikeJdbcSessionHolder}
import crossroad0201.dddonscala.infrastructure.{EntityMetaDataCreatorImpl, UUIDEntityIdGenerator}
import scalikejdbc._

import scala.util.Failure

trait InfrastructureAware extends TransactionAware {
  implicit val entityIdGenerator:     EntityIdGenerator     = UUIDEntityIdGenerator
  implicit val entityMetaDataCreator: EntityMetaDataCreator = EntityMetaDataCreatorImpl

  implicit val infraErrorHandler: Throwable => ServiceError = {
    case e: OptimisticLockException => ConflictedError(e.id)
    case e => SystemError(e)
  }

  def tx[A](f: UnitOfWork => A): A = {
    // FIXME リファクタリングしたい
    using(DB(ConnectionPool.borrow())) { db =>
      try {
        db.begin

        val result = db withinTx { dbs =>
          val uow = new UnitOfWork with ScalikeJdbcSessionHolder {
            override val dbSession = dbs
          }
          f(uow)
        }

        /*
         * Left（Eitherのエラー）、Failure（Tryのエラー）のときは
         * トランザクションをロールバックし、
         * それ以外の場合はコミットします。
         */
        result match {
          case Left(_)    => db.rollbackIfActive
          case Failure(_) => db.rollbackIfActive
          case _          => db.commit
        }

        result

      } catch {
        case e: Throwable =>
          db.rollbackIfActive
          throw e
      }
    }
  }

  def txReadonly[A](f: UnitOfWork => A): A = {
    using(DB(ConnectionPool.borrow())) { db =>
      db readOnly { dbs =>
        val uow = new UnitOfWork with ScalikeJdbcSessionHolder {
          override val dbSession = dbs
        }
        f(uow)
      }
    }
  }

}
