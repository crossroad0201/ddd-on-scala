package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.DomainEvent
import crossroad0201.dddonscala.domain.user.UserId

trait TaskEvent extends DomainEvent {
  val taskId: TaskId
}

case class TaskCreated(
    taskId: TaskId,
    name:   TaskName
) extends TaskEvent

case class TaskAssigned(
    taskId:     TaskId,
    assigneeId: UserId
) extends TaskEvent
