package crossroad0201.dddonscala.infrastructure

import crossroad0201.dddonscala.domain.EntityId

package object rdb {

  type Version = Int

  case class OptimisticLockException(id: EntityId, version: Version)
      extends RuntimeException(s"Optimistic lock error at $id with $version.")

}
