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
object TaskCreated {
  def at(task: UnAssignedTask): TaskCreated =
    TaskCreated(
      taskId = task.id,
      name   = task.name
    )
}

case class TaskAssigned(
    taskId:     TaskId,
    assigneeId: UserId
) extends TaskEvent
object TaskAssigned {
  def at(task: AssignedTask): TaskAssigned =
    TaskAssigned(
      taskId     = task.id,
      assigneeId = task.assigneeId
    )
}
