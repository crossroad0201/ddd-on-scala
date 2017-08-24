package crossroad0201.dddonscala.infrastructure

import crossroad0201.dddonscala.domain.UnitOfWork
import crossroad0201.dddonscala.infrastructure.rdb.ScalikeJdbcSessionHolder
import scalikejdbc._

import scala.util.Failure

trait TransactionAware {

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

  def txReadOnly[A](f: UnitOfWork => A): A = {
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
