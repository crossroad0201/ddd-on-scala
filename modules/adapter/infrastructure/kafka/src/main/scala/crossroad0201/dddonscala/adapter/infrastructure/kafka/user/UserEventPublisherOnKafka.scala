package crossroad0201.dddonscala.adapter.infrastructure.kafka.user

import crossroad0201.dddonscala.domain.UnitOfWork
import crossroad0201.dddonscala.domain.user.{UserEvent, UserEventPublisher}

trait UserEventPublisherOnKafka extends UserEventPublisher {

  override def publish[EVENT <: UserEvent](event: EVENT)(implicit uow: UnitOfWork) = ??? // FIXME

}
