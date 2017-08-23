package crossroad0201.dddonscala.domain.user

import crossroad0201.dddonscala.domain.{entity, Entity}

@entity
case class User(
    id: UserId
) extends Entity[UserId]
