package crossroad0201.dddonscala.adapter.infrastructure

import crossroad0201.dddonscala.domain.EntityId
import crossroad0201.dddonscala.infrastructure.Version

package object rdb {

  case class OptimisticLockException(id: EntityId, version: Version)
      extends RuntimeException(s"Optimistic lock error at $id with $version.")

}
