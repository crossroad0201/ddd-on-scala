package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.task.TaskState.{Closed, Opened}
import crossroad0201.dddonscala.domain.user.{User, UserId}
import crossroad0201.dddonscala.domain.{DomainResult, Entity}

// 状態を継承で表現する版

sealed trait Task extends Entity[TaskId] {
  val id:       TaskId
  val name:     TaskName
  val state:    TaskState
  val authorId: UserId
  val comments: Comments

  def close:  Either[TaskAlreadyClosed, DomainResult[Task, TaskClosed]]
  def reOpen: Either[TaskAlreadyOpened, DomainResult[Task, TaskReOpened]]

  def assign(assignee: User): Either[TaskAlreadyClosed, DomainResult[AssignedTask, TaskAssigned]]

  def addComment(comment: Comment): DomainResult[Task, TaskCommented]
}

case class UnAssignedTask(
    id:       TaskId,
    name:     TaskName,
    state:    TaskState = TaskState.Opened,
    authorId: UserId,
    comments: Comments = Comments.Nothing
) extends Task {

  override def close: Either[TaskAlreadyClosed, DomainResult[UnAssignedTask, TaskClosed]] = {
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

  override def reOpen: Either[TaskAlreadyOpened, DomainResult[UnAssignedTask, TaskReOpened]] = {
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

  override def assign(assignee: User): Either[TaskAlreadyClosed, DomainResult[AssignedTask, TaskAssigned]] = {
    state match {
      case Opened =>
        val task = AssignedTask(
          id         = id,
          name       = name,
          state      = state,
          authorId   = authorId,
          assigneeId = assignee.id,
          comments   = comments
        )
        val event = TaskAssigned(
          taskId     = task.id,
          assigneeId = task.assigneeId
        )
        Right(DomainResult(task, event))
      case _ => Left(TaskAlreadyClosed(this))
    }
  }

  // FIXME 各サブクラスで実装するのは冗長...
  override def addComment(comment: Comment): DomainResult[UnAssignedTask, TaskCommented] = {
    val task = copy(
      comments = comments add comment
    )
    val event = TaskCommented(
      taskId      = task.id,
      commenterId = comment.commenterId,
      message     = comment.message
    )
    DomainResult(task, event)
  }
}

case class AssignedTask(
    id:         TaskId,
    name:       TaskName,
    state:      TaskState = TaskState.Opened,
    authorId:   UserId,
    assigneeId: UserId,
    comments:   Comments = Comments.Nothing
) extends Task {

  override def close: Either[TaskAlreadyClosed, DomainResult[AssignedTask, TaskClosed]] = {
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

  override def reOpen: Either[TaskAlreadyOpened, DomainResult[AssignedTask, TaskReOpened]] = {
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

  def unAssign: Either[TaskAlreadyClosed, DomainResult[UnAssignedTask, TaskUnAssigned]] = {
    state match {
      case Opened =>
        val task = UnAssignedTask(
          id       = id,
          name     = name,
          state    = state,
          authorId = authorId,
          comments = comments
        )
        val event = TaskUnAssigned(
          taskId = task.id
        )
        Right(DomainResult(task, event))
      case _ => Left(TaskAlreadyClosed(this))
    }
  }

  override def assign(assignee: User): Either[TaskAlreadyClosed, DomainResult[AssignedTask, TaskAssigned]] = {
    state match {
      case Opened =>
        val task = copy(
          assigneeId = assignee.id
        )
        val event = TaskAssigned(
          taskId     = task.id,
          assigneeId = task.assigneeId
        )
        Right(DomainResult(task, event))
      case _ => Left(TaskAlreadyClosed(this))
    }
  }

  override def addComment(comment: Comment): DomainResult[AssignedTask, TaskCommented] = {
    val task = copy(
      comments = comments add comment
    )
    val event = TaskCommented(
      taskId      = task.id,
      commenterId = comment.commenterId,
      message     = comment.message
    )
    DomainResult(task, event)
  }
}
