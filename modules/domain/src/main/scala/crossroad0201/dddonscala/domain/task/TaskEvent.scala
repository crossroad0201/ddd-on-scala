package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.{domainevent, DomainEvent}
import crossroad0201.dddonscala.domain.user.UserId

sealed trait TaskEvent extends DomainEvent {
  val taskId: TaskId
}

@domainevent
case class TaskCreated(
    taskId:   TaskId,
    name:     TaskName,
    authorId: UserId
) extends TaskEvent

@domainevent
case class TaskAssigned(
    taskId:     TaskId,
    assigneeId: UserId
) extends TaskEvent

@domainevent
case class TaskUnAssigned(
    taskId: TaskId
) extends TaskEvent

@domainevent
case class TaskCommented(
    taskId:      TaskId,
    commenterId: UserId,
    message:     CommentMessage
) extends TaskEvent

@domainevent
case class TaskClosed(
    taskId: TaskId
) extends TaskEvent

@domainevent
case class TaskReOpened(
    taskId: TaskId
) extends TaskEvent
