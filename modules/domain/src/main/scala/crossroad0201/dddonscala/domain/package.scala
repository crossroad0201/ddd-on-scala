package crossroad0201.dddonscala

package object domain {

  // FIXME AnyVal を継承する？？
  trait Value[T] {
    val value: T
  }

  case class DomainResult[+ENTITY <: Entity[_ <: EntityId], +EVENT <: DomainEvent](entity: ENTITY, event: EVENT)

}
