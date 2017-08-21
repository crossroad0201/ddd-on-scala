package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.{DomainResult, Entity}
import crossroad0201.dddonscala.domain.user.{User, UserId}

trait Task extends Entity[TaskId] {
  type SelfType <: Task

  val id:       TaskId
  val name:     TaskName
  val authorId: UserId
  val comments: Comments

  // FIXME 引数は　Assignee のほうがいい？
  def assign(assignee: User): DomainResult[AssignedTask, TaskAssigned]

  def addComment(comment: Comment): DomainResult[SelfType, TaskCommented]
}

// FIXME 状態を型ヒエラルキーで表すより、トレイトミックスインのほうがいい？
case class UnAssignedTask(
    id:       TaskId,
    name:     TaskName,
    authorId: UserId,
    comments: Comments = Comments.Nothing
) extends Task {
  override type SelfType = UnAssignedTask

  override def assign(assignee: User): DomainResult[AssignedTask, TaskAssigned] = {
    val task = AssignedTask(
      id         = id,
      name       = name,
      authorId   = authorId,
      assigneeId = assignee.id,
      comments   = comments
    )
    val event = TaskAssigned(
      taskId     = task.id,
      assigneeId = task.assigneeId
    )
    DomainResult(task, event)
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
    authorId:   UserId,
    assigneeId: UserId,
    comments:   Comments
) extends Task {
  override type SelfType = AssignedTask

  override def assign(assignee: User): DomainResult[AssignedTask, TaskAssigned] = {
    val task = copy(
      assigneeId = assignee.id
    )
    val event = TaskAssigned(
      taskId     = task.id,
      assigneeId = task.assigneeId
    )
    DomainResult(task, event)
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
