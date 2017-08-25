package crossroad0201.dddonscala.domain.user

import crossroad0201.dddonscala.domain.{Entity, EntityMetaData}

// FIXME Userの作成（無からのファクトリの例として）

case class User(
    id:       UserId,
    metaData: EntityMetaData
) extends Entity[UserId]
