package crossroad0201.dddonscala.domain

import crossroad0201.dddonscala.domain.user.User

package object task {
  import scala.language.implicitConversions

  case class TaskId(value: String) extends EntityId
  object TaskId {
    def newId(implicit idGen: EntityIdGenerator): TaskId =
      TaskId(idGen.genId())
  }

  case class TaskName(value: String) extends Value[String]

  case class CommentMessage(value: String) extends Value[String]

  sealed abstract class TaskState
  object TaskState {
    case object Opened extends TaskState
    case object Closed extends TaskState
  }

  implicit def asAuthor(user: User): Author = Author(user)
  case class Author(user: User) {
    def createTask(name: TaskName)(implicit idGen: EntityIdGenerator): DomainResult[UnAssignedTask, TaskCreated] = {
      val task = UnAssignedTask(
        id       = TaskId.newId,
        name     = name,
        authorId = user.id
      )
      val event = TaskCreated(
        taskId = task.id,
        name   = task.name
      )
      DomainResult(task, event)
    }
  }

  implicit def asAssignee(user: User): Assignee = Assignee(user)
  case class Assignee(user: User) {
    def assignTo(task: Task): Either[TaskAlreadyClosed, DomainResult[AssignedTask, TaskAssigned]] = task assign user
  }

  implicit def asCommenter(user: User): Commenter = Commenter(user)
  case class Commenter(user: User) {
    def commentTo[TASK <: Task](task: TASK, message: CommentMessage): DomainResult[TASK, TaskCommented] = {
      // TODO asInstanceOf したくない
      task.addComment(Comment(message, user.id)).asInstanceOf[DomainResult[TASK, TaskCommented]]
    }
  }
}
