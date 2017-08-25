package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.DomainEvent
import crossroad0201.dddonscala.domain.user.UserId

sealed trait TaskEvent extends DomainEvent {
  val taskId: TaskId
}

case class TaskCreated(
    taskId:   TaskId,
    name:     TaskName,
    authorId: UserId
) extends TaskEvent

case class TaskAssigned(
    taskId:     TaskId,
    assigneeId: UserId
) extends TaskEvent

case class TaskUnAssigned(
    taskId: TaskId
) extends TaskEvent

case class TaskCommented(
    taskId:      TaskId,
    commenterId: UserId,
    message:     CommentMessage
) extends TaskEvent

case class TaskClosed(
    taskId: TaskId
) extends TaskEvent

case class TaskReOpened(
    taskId: TaskId
) extends TaskEvent
