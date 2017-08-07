package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.{ DomainResult, Entity }
import crossroad0201.dddonscala.domain.user.{ User, UserId }

trait Task extends Entity[TaskId] {
  val id: TaskId
  val name: TaskName
  val authorId: UserId

  def assign(assignee: User): DomainResult[AssignedTask, TaskAssigned]
}

// FIXME 状態を型ヒエラルキーで表すより、トレイトミックスインのほうがいい？
case class UnAssignedTask(
    id:       TaskId,
    name:     TaskName,
    authorId: UserId
) extends Task {
  override def assign(assignee: User): DomainResult[AssignedTask, TaskAssigned] = {
    val task = AssignedTask(
      id         = id,
      name       = name,
      authorId   = authorId,
      assigneeId = assignee.id
    )
    val event = TaskAssigned(
      taskId     = task.id,
      assigneeId = task.assigneeId
    )
    DomainResult(task, event)
  }
}

case class AssignedTask(
    id:         TaskId,
    name:       TaskName,
    authorId:   UserId,
    assigneeId: UserId
) extends Task {
  override def assign(assignee: User): DomainResult[AssignedTask, TaskAssigned] = {
    val task = copy(assigneeId = assignee.id)
    val event = TaskAssigned(
      taskId     = task.id,
      assigneeId = task.assigneeId
    )
    DomainResult(task, event)
  }
}
