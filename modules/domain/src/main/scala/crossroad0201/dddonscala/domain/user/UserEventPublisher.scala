package crossroad0201.dddonscala.domain.user

import crossroad0201.dddonscala.domain.UnitOfWork

import scala.util.Try

trait UserEventPublisher {

  def publish[EVENT <: UserEvent](event: EVENT)(implicit uow: UnitOfWork): Try[EVENT]

}
