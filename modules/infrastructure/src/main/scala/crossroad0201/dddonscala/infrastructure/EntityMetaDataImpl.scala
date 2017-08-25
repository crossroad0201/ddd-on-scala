package crossroad0201.dddonscala.infrastructure

import crossroad0201.dddonscala.domain.{EntityMetaData, EntityMetaDataCreator}
import crossroad0201.dddonscala.infrastructure.rdb.Version

case class EntityMetaDataImpl(
    version: Version
) extends EntityMetaData

object EntityMetaDataCreatorImpl extends EntityMetaDataCreator {
  override def create = EntityMetaDataImpl(0)
}
