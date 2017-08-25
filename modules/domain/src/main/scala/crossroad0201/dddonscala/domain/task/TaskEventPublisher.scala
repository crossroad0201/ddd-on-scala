package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.UnitOfWork

import scala.util.Try

// FIXME サンプル実装を作る
trait TaskEventPublisher {

  def publish[EVENT <: TaskEvent](event: EVENT)(implicit uow: UnitOfWork): Try[EVENT]

}
