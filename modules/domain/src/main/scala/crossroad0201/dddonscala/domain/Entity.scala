package crossroad0201.dddonscala.domain

trait Entity[ID <: EntityId] {
  val id: ID

  override def equals(obj: Any): Boolean =
    // FIXME 同じエンティティ型かどうか
    obj match {
      case that: Entity[_] => this.id == that.id
      case _               => false
    }

  override def hashCode(): Int = 31 + id.hashCode

}

trait EntityId extends Value[String]

trait EntityIdGenerator {
  def genId(): String
}
