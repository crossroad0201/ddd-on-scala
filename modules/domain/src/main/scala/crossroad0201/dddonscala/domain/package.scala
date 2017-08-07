package crossroad0201.dddonscala

package object domain {

  // FIXME インフラ層でプリミティブ値とimplicit wrap/unwrapする
  trait Value[T] {
    val value: T
  }

  case class DomainResult[+ENTITY <: Entity[_ <: EntityId], +EVENT <: DomainEvent](entity: ENTITY, event: EVENT)
  type ErrorOr[+ENTITY <: Entity[_ <: EntityId], +EVENT <: DomainEvent] = Either[DomainError, DomainResult[ENTITY, EVENT]]

}

