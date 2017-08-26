package crossroad0201.dddonscala.adapter.infrastructure.kafka.task

import crossroad0201.dddonscala.domain.UnitOfWork
import crossroad0201.dddonscala.domain.task.{TaskEvent, TaskEventPublisher}

trait TaskEventPublisherOnKafka extends TaskEventPublisher {

  override def publish[EVENT <: TaskEvent](event: EVENT)(implicit uow: UnitOfWork) =
    throw new UnsupportedOperationException("このサンプルでは未実装です。")

}
