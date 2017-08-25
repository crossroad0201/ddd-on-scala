package crossroad0201.dddonscala.adapter.controller.sample

import crossroad0201.dddonscala.domain.EntityIdGenerator
import crossroad0201.dddonscala.infrastructure
import crossroad0201.dddonscala.infrastructure.{EntityMetaDataCreatorImpl, TransactionAwareImpl, UUIDEntityIdGenerator}

trait InfrastructureAware extends TransactionAwareImpl {
  implicit val entityIdGenerator: EntityIdGenerator = UUIDEntityIdGenerator
  implicit val entityMetaDataCreator = EntityMetaDataCreatorImpl
  implicit val infraErrorHandler     = infrastructure.infraErrorHandler
}
