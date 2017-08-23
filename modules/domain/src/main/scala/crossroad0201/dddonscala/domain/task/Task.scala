package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.task.TaskState.{Closed, Opened}
import crossroad0201.dddonscala.domain.{entity, DomainResult, Entity}
import crossroad0201.dddonscala.domain.user.{User, UserId}

@entity
case class Task(
    id:         TaskId,
    name:       TaskName,
    state:      TaskState = TaskState.Opened,
    authorId:   UserId,
    assignment: Assignment = Assignment.notAssigned,
    comments:   Comments = Comments.Nothing
) extends Entity[TaskId] {

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
    this match {
      case Task.is(Opened, a @ Assigned(_)) =>
        val task = copy(assignment = a.clear)
        val event = TaskUnAssigned(
          taskId = task.id
        )
        Right(DomainResult(task, event))
      case Task.is(Opened, Assignment.notAssigned) =>
        val event = TaskUnAssigned(
          taskId = id
        )
        Right(DomainResult(this, event))
      case Task.is(Closed, _) => Left(TaskAlreadyClosed(this))
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
}
object Task {
  /*
   * ケースクラスでは、コンパニオンオブジェクトにすべてのプロパティに展開する unapply メソッドが
   * 自動生成されるため、独自に unapply メソッドを定義することができないので、
   * このようなインナーオブジェクトを定義して unapply メソッドを定義しています。
   *
   * こうすることで match式 で
   *   case Task(_, _, Opened, _, Assigned, _) => ...
   * を
   *   case Task.is(Opened, Assigned) => ...
   * のように、簡潔に任意のプロパティだけのパターンマッチを書けるようになります。
   */
  object is {
    def unapply(task: Task): Option[(TaskState, Assignment)] = Some((task.state, task.assignment))
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
