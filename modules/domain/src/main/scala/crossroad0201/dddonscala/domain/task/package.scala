package crossroad0201.dddonscala.domain

import crossroad0201.dddonscala.domain.task.{
  AssignedTask,
  Task,
  TaskId,
  TaskName,
  UnAssignedTask
}
import crossroad0201.dddonscala.domain.user.User

package object task {
  import scala.language.implicitConversions

  case class TaskId(value: String) extends EntityId
  object TaskId {
    def newId(implicit idGen: EntityIdGenerator): TaskId =
      TaskId(idGen.genId())
  }

  case class TaskName(value: String) extends Value[String]

  implicit def asTaskAuthor(user: User): Author = Author(user)
  implicit def asTaskAssignee(user: User): Assignee = Assignee(user)

}

case class Author(user: User) {
  def createTask(name: TaskName)(
    implicit
    idGen: EntityIdGenerator): UnAssignedTask = {
    UnAssignedTask(
      id       = TaskId.newId,
      name     = name,
      authorId = user.id)
  }
}

case class Assignee(user: User) {
  def assignTo(task: Task): AssignedTask = task.assign(user)
}
