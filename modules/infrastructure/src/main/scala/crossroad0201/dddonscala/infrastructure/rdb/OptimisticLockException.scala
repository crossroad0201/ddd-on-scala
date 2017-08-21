package crossroad0201.dddonscala.infrastructure.rdb

import crossroad0201.dddonscala.domain.EntityId

case class OptimisticLockException(id: EntityId, version: Version)
    extends RuntimeException(s"Optimistic lock error at $id with version $version.")
