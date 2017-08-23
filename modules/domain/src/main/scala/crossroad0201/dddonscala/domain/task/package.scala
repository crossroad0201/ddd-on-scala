package crossroad0201.dddonscala.domain

import crossroad0201.dddonscala.domain.user.User

package object task {
  import scala.language.implicitConversions

  @valueobject
  case class TaskId(value: String) extends EntityId
  object TaskId {
    def newId(implicit idGen: EntityIdGenerator): TaskId =
      TaskId(idGen.genId())
  }

  @valueobject
  case class TaskName(value: String) extends Value[String]

  @valueobject
  case class CommentMessage(value: String) extends Value[String]

  // FIXME ステータスによって実行可否が異なる振る舞いは、この列挙型に聞くようにしたい
  @valueobject
  sealed abstract class TaskState
  object TaskState {
    case object Opened extends TaskState
    case object Closed extends TaskState
  }

  implicit def asAuthor(user: User): Author = Author(user)
  @factory
  case class Author(user: User) {
    def createTask(name: TaskName)(implicit idGen: EntityIdGenerator): DomainResult[Task, TaskCreated] = {
      val task = Task(
        id       = TaskId.newId,
        name     = name,
        authorId = user.id
      )
      val event = TaskCreated(
        taskId   = task.id,
        name     = task.name,
        authorId = task.authorId
      )
      DomainResult(task, event)
    }
  }

  implicit def asAssignee(user: User): Assignee = Assignee(user)
  case class Assignee(user: User) {
    def assignTo(task: Task): Either[TaskAlreadyClosed, DomainResult[Task, TaskAssigned]] =
      task.assign(user)
  }

  implicit def asCommenter(user: User): Commenter = Commenter(user)
  case class Commenter(user: User) {
    def commentTo(task: Task, message: CommentMessage): DomainResult[Task, TaskCommented] =
      task.addComment(Comment(message, user.id))
  }
}
