package crossroad0201.dddonscala.domain

trait Entity[ID <: EntityId] {
  val id: ID

  // FIXME 楽観排他用のバージョンをどうする？

  override def equals(obj: Any): Boolean =
    obj match {
      case that: Entity[_] => this.getClass == that.getClass && this.id == that.id
      case _ => false
    }

  override def hashCode(): Int = 31 + id.hashCode

}

trait EntityId extends Value[String]

trait EntityIdGenerator {
  def genId(): String
}
