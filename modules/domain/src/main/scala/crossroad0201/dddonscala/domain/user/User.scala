package crossroad0201.dddonscala.domain.user

import crossroad0201.dddonscala.domain._

case class User(
    id:       UserId,
    name:     UserName,
    metaData: EntityMetaData
) extends Entity[UserId]

object User {
  // NOTE: 「単独で生み出されるエンティティ」を生成するファクトリは、コンパニオンオブジェクトのメソッドとして定義します。
  def create(name:                            UserName)(implicit idGen: EntityIdGenerator,
                             metaDataCreator: EntityMetaDataCreator): DomainResult[User, UserCreated] = {
    val user = User(
      id       = UserId.newId,
      name     = name,
      metaData = metaDataCreator.create
    )
    val event = UserCreated(
      userId   = user.id,
      userName = user.name
    )
    DomainResult(user, event)
  }
}
