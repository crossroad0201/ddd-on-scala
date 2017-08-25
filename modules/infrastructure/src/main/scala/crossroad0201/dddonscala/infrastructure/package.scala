package crossroad0201.dddonscala

import java.util.UUID

import crossroad0201.dddonscala.application.{ConflictedError, ServiceError, SystemError}
import crossroad0201.dddonscala.domain.EntityIdGenerator
import crossroad0201.dddonscala.infrastructure.rdb.OptimisticLockException

package object infrastructure {

  val infraErrorHandler: Throwable => ServiceError = {
    case e: OptimisticLockException => ConflictedError(e.id)
    case e => SystemError(e)
  }

  object UUIDEntityIdGenerator extends EntityIdGenerator {
    override def genId() = UUID.randomUUID().toString
  }
}
