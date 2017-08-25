package crossroad0201.dddonscala.domain

trait Entity[ID <: EntityId] {
  val id:       ID
  val metaData: EntityMetaData

  override def equals(obj: Any): Boolean =
    obj match {
      case that: Entity[_] => this.getClass == that.getClass && this.id == that.id
      case _ => false
    }

  override def hashCode(): Int = 31 + id.hashCode

}

trait EntityId extends Value[String]

trait EntityMetaData
trait EntityMetaDataCreator {
  def create: EntityMetaData
}

trait EntityIdGenerator {
  def genId(): String
}
