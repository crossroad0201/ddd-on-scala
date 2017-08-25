package crossroad0201.dddonscala.domain.user

import crossroad0201.dddonscala.domain._

case class User(
    id:       UserId,
    metaData: EntityMetaData
) extends Entity[UserId]

object User {
  def create(implicit idGen:  EntityIdGenerator,
             metaDataCreator: EntityMetaDataCreator): DomainResult[User, UserCreated] = {
    val user = User(
      id       = UserId.newId,
      metaData = metaDataCreator.create
    )
    val event = UserCreated(
      userId = user.id
    )
    DomainResult(user, event)
  }
}
