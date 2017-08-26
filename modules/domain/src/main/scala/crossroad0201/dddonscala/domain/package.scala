package crossroad0201.dddonscala

package object domain {

  trait Value[T] extends Any {
    def value: T
  }

  case class DomainResult[+ENTITY <: Entity[_ <: EntityId], +EVENT <: DomainEvent](entity: ENTITY, event: EVENT)

}
