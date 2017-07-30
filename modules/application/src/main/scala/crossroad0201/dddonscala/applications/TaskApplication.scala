package crossroad0201.dddonscala.applications

import crossroad0201.dddonscala.domain.EntityIdGenerator
import crossroad0201.dddonscala.domain.task.{
  Task,
  TaskEventPublisher,
  TaskId,
  TaskName,
  TaskRepository
}
import crossroad0201.dddonscala.domain.user.User

trait TaskApplication {
  implicit val entityIdGenerator: EntityIdGenerator
  val taskRepository: TaskRepository
  val taskEventPublisher: TaskEventPublisher

  def createNewTask(name: TaskName, user: User): Either[ApplicationError, Task] = {
    import crossroad0201.dddonscala.domain.task._

    for {
      savedTask <- taskRepository.save(
        user createTask name)
      _ <- taskEventPublisher publish (TaskCreated at savedTask)
    } yield savedTask
  }

  def assignToTask(taskId: TaskId, user: User): Either[ApplicationError, Task] = {
    import crossroad0201.dddonscala.domain.task._

    // FIXME 通らん（１つのfor式のなかで効くimplicitは１種類だけ？）
    val x = for {
      task <- shouldExists(taskRepository.get(taskId).get) // FIXME Try[Option[T]] -> Either[AppError, T]
      assignedTask = user.assignTo(task)
      savedTask <- taskRepository.save(assignedTask)
      _ <- taskEventPublisher publish (TaskAssigned at savedTask)
    } yield savedTask

    shouldExists(taskRepository.get(TaskId("xxxx")).get)
  }

}
