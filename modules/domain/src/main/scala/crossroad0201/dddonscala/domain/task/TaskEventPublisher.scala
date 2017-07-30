package crossroad0201.dddonscala.domain.task

import scala.util.Try

trait TaskEventPublisher {

  def publish(event: TaskCreated): Try[TaskCreated]
  def publish(event: TaskAssigned): Try[TaskAssigned]

}
