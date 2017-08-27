package crossroad0201.dddonscala.domain

trait Entity[ID <: EntityId] {
  val id:       ID
  val metaData: EntityMetaData

  // NOTE: 型 と ID でエンティティの同一性を判断します。
  override def equals(obj: Any): Boolean =
    obj match {
      case that: Entity[_] => this.getClass == that.getClass && this.id == that.id
      case _ => false
    }

  override def hashCode(): Int = 31 + id.hashCode

}

trait EntityId extends Any with Value[String]

// NOTE: 楽観排他制御用のバージョンなど、ドメインの関心事ではないがエンティティで保持する必要がある情報を扱います
trait EntityMetaData
trait EntityMetaDataCreator {
  def create: EntityMetaData
}

// NOTE: エンティティIDの採番方法を抽象化します。ユニットテスト時には予測可能な方法で採番できます。
trait EntityIdGenerator {
  def genId(): String
}
