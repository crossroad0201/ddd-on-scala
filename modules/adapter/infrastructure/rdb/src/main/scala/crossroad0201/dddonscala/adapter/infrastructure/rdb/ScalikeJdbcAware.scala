package crossroad0201.dddonscala.adapter.infrastructure.rdb

import crossroad0201.dddonscala.domain.UnitOfWork
import scalikejdbc.DBSession

trait ScalikeJdbcAware {

  implicit def getDBSession(implicit uof: UnitOfWork): DBSession = uof.asInstanceOf[ScalikeJdbcSessionHolder].dbSession

}

trait ScalikeJdbcSessionHolder {
  val dbSession: DBSession
}
