package crossroad0201.dddonscala.infrastructure

import crossroad0201.dddonscala.domain.{EntityMetaData, EntityMetaDataCreator}

object EntityMetaDataCreatorImpl extends EntityMetaDataCreator {
  override def create = EntityMetaDataImpl(0)
}

case class EntityMetaDataImpl(
    version: Version
) extends EntityMetaData
