package crossroad0201.dddonscala.domain

package object user {

  case class UserId(value: String) extends EntityId
  object UserId {
    def newId(implicit idGen: EntityIdGenerator): UserId = UserId(idGen.genId())
  }

}
