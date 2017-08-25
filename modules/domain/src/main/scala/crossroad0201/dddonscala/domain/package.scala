package crossroad0201.dddonscala

package object domain {

  // FIXME AnyVal を継承する？？
  // FIXME インフラ層でプリミティブ値とimplicit wrap/unwrapする
  trait Value[T] {
    val value: T
  }

  case class DomainResult[+ENTITY <: Entity[_ <: EntityId], +EVENT <: DomainEvent](entity: ENTITY, event: EVENT)

}
