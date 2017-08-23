package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.task.TaskState.{Closed, Opened}
import crossroad0201.dddonscala.domain.{DomainResult, Entity}
import crossroad0201.dddonscala.domain.user.{User, UserId}

case class Task(
    id:         TaskId,
    name:       TaskName,
    state:      TaskState = TaskState.Opened,
    authorId:   UserId,
    assignment: Assignment = Assignment.notAssigned,
    comments:   Comments = Comments.Nothing
) extends Entity[TaskId] {

  def close: Either[TaskAlreadyClosed, DomainResult[Task, TaskClosed]] = {
    state match {
      case Opened =>
        val task = copy(
          state = TaskState.Closed
        )
        val event = TaskClosed(
          taskId = task.id
        )
        Right(DomainResult(task, event))
      case _ => Left(TaskAlreadyClosed(this))
    }
  }

  def reOpen: Either[TaskAlreadyOpened, DomainResult[Task, TaskReOpened]] = {
    state match {
      case Closed =>
        val task = copy(
          state = TaskState.Opened
        )
        val event = TaskReOpened(
          taskId = task.id
        )
        Right(DomainResult(task, event))
      case _ => Left(TaskAlreadyOpened(this))
    }
  }

  def assign(assignee: User): Either[TaskAlreadyClosed, DomainResult[Task, TaskAssigned]] = {
    state match {
      case Opened =>
        val task = copy(assignment = Assignment.assignedBy(assignee.id))
        val event = TaskAssigned(
          taskId     = task.id,
          assigneeId = assignee.id
        )
        Right(DomainResult(task, event))
      case _ => Left(TaskAlreadyClosed(this))
    }
  }

  def unAssign: Either[TaskAlreadyClosed, DomainResult[Task, TaskUnAssigned]] = {
    // FIXME すでに unAssign されていたら...? べき等にする? べき等なときにイベント発行する？
    state match {
      case Opened =>
        val task = copy(assignment = Assignment.notAssigned) // FIXME clearを呼ぶようにしたいが...
        val event = TaskUnAssigned(
          taskId = task.id
        )
        Right(DomainResult(task, event))
      case _ => Left(TaskAlreadyClosed(this))
    }
  }

  def addComment(comment: Comment): DomainResult[Task, TaskCommented] = {
    val task = copy(
      comments = comments.add(comment)
    )
    val event = TaskCommented(
      taskId      = task.id,
      commenterId = comment.commenterId,
      message     = comment.message
    )
    DomainResult(task, event)
  }
}

trait Assignment
case class Assigned(assigneeId: UserId) extends Assignment {
  def clear = Assignment.notAssigned
}
object Assignment {
  case object notAssigned extends Assignment
  def assignedBy(assigneeId: UserId): Assigned = Assigned(assigneeId)
}
