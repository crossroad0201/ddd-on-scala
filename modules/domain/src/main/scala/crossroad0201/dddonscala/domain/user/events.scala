package crossroad0201.dddonscala.domain.user

import crossroad0201.dddonscala.domain.DomainEvent

trait UserEvent extends DomainEvent {
  val userId: UserId
}

case class UserCreated(
    userId: UserId
) extends UserEvent
