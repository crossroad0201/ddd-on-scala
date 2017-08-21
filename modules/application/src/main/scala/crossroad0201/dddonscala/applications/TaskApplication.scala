package crossroad0201.dddonscala.applications

import crossroad0201.dddonscala.domain.EntityIdGenerator
import crossroad0201.dddonscala.domain.task.{
  CommentMessage,
  Task,
  TaskEventPublisher,
  TaskId,
  TaskName,
  TaskRepository
}
import crossroad0201.dddonscala.domain.user.User

trait TaskApplication {
  implicit val entityIdGenerator: EntityIdGenerator
  val taskRepository:             TaskRepository
  val taskEventPublisher:         TaskEventPublisher

  // FIXME Unit of Work（DBコミット後に、イベントのパブリッシュする）

  def createNewTask(name: TaskName, user: User): Either[ApplicationError, Task] = {
    import crossroad0201.dddonscala.domain.task._

    // FIXME 集約ごとに実装を分けるべきかも
    val createdTask = user createTask name
    for {
      savedTask <- taskRepository save createdTask.entity
      _         <- taskEventPublisher publish createdTask.event
    } yield savedTask
  }

  def assignToTask(taskId: TaskId, user: User): Either[ApplicationError, Task] = {
    import crossroad0201.dddonscala.domain.task._

    for {
      task <- shouldExists(taskRepository.get(taskId))
      assignedTask = user assignTo task
      savedTask <- taskRepository save assignedTask.entity
      _         <- taskEventPublisher publish assignedTask.event
    } yield savedTask
  }

  def commentToTask(taskId: TaskId, message: CommentMessage, user: User): Either[ApplicationError, Task] = {
    import crossroad0201.dddonscala.domain.task._

    for {
      task <- shouldExists(taskRepository.get(taskId))
      commentedTask = user commentTo (task, message)
      savedTask <- taskRepository save commentedTask.entity
      _         <- taskEventPublisher publish commentedTask.event
    } yield savedTask
  }

  // TODO タスクの完了（Assigneeしか実行できない）

}
