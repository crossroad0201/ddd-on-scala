package crossroad0201.dddonscala

import java.util.UUID

import crossroad0201.dddonscala.domain.EntityIdGenerator

package object infrastructure {

  object UUIDEntityIdGenerator extends EntityIdGenerator {
    override def genId() = UUID.randomUUID().toString
  }

}
