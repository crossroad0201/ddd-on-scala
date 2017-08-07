package crossroad0201.dddonscala.applications

import crossroad0201.dddonscala.domain.EntityIdGenerator
import crossroad0201.dddonscala.domain.task.{ Task, TaskEventPublisher, TaskId, TaskName, TaskRepository }
import crossroad0201.dddonscala.domain.user.User

trait TaskApplication {
  implicit val entityIdGenerator: EntityIdGenerator
  val taskRepository: TaskRepository
  val taskEventPublisher: TaskEventPublisher

  def createNewTask(name: TaskName, user: User): Either[ApplicationError, Task] = {
    import crossroad0201.dddonscala.domain.task._

    val createdTask = user createTask name
    for {
      savedTask <- taskRepository save createdTask.entity
      _ <- taskEventPublisher publish createdTask.event
    } yield savedTask
  }

  def assignToTask(taskId: TaskId, user: User): Either[ApplicationError, Task] = {
    import crossroad0201.dddonscala.domain.task._

    for {
      task <- shouldExists(taskRepository.get(taskId))
      assignedTask = user assignTo task
      savedTask <- taskRepository save assignedTask.entity
      _ <- taskEventPublisher publish assignedTask.event
    } yield savedTask
  }

}
