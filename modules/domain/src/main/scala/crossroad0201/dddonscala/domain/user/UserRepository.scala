package crossroad0201.dddonscala.domain.user

import crossroad0201.dddonscala.domain.UnitOfWork

import scala.util.Try

trait UserRepository {

  def get(id: UserId)(implicit uow: UnitOfWork): Try[Option[User]]

}
