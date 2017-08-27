package crossroad0201.dddonscala

// FIXME ドメインサービスの例がない

package object domain {

  // NOTE: AnyValにミックスインできるように、Anyを継承します
  trait Value[T] extends Any {
    def value: T
  }

  case class DomainResult[+ENTITY <: Entity[_ <: EntityId], +EVENT <: DomainEvent](entity: ENTITY, event: EVENT)

}
