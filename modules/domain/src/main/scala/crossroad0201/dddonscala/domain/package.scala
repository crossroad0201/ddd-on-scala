package crossroad0201.dddonscala.domain

package object domain {

  type ErrorOr[+ENTITY <: Entity[_ <: EntityId], +EVENT <: DomainEvent] = Either[DomainError, DomainResult[ENTITY, EVENT]]

}

// FIXME インフラ層でプリミティブ値とimplicit wrap/unwrapする
trait Value[T] {
  val value: T
}

trait EntityId extends Value[String]

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

trait EntityIdGenerator {
  def genId(): String
}

trait DomainEvent

trait DomainError

case class DomainResult[+ENTITY <: Entity[_ <: EntityId], +EVENT <: DomainEvent](entity: ENTITY, event: EVENT)
