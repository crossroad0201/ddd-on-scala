package crossroad0201.dddonscala.applications

import crossroad0201.dddonscala.domain.EntityIdGenerator
import crossroad0201.dddonscala.domain.task.{
  AssignedTask,
  CommentMessage,
  Task,
  TaskAlreadyClosed,
  TaskAlreadyOpened,
  TaskEventPublisher,
  TaskId,
  TaskName,
  TaskRepository
}
import crossroad0201.dddonscala.domain.user.User

import scala.language.postfixOps

trait TaskApplication {
  implicit val entityIdGenerator: EntityIdGenerator
  val taskRepository:             TaskRepository
  val taskEventPublisher:         TaskEventPublisher

  // FIXME 実行ユーザーを implicit で
  // FIXME Unit of Work（DBコミット後に、イベントのパブリッシュする）

  private implicit val taskAlreadyClosedHandler: TaskAlreadyClosed => ApplicationError = (e) =>
    InvalidTaskOperationError(e.task.id)
  private implicit val taskAlreadyOpenedHandler: TaskAlreadyOpened => ApplicationError = (e) =>
    InvalidTaskOperationError(e.task.id)

  def createNewTask(name: TaskName, user: User): Either[ApplicationError, Task] = {
    import crossroad0201.dddonscala.domain.task._

    val createdTask = user createTask name
    for {
      savedTask <- taskRepository.save(createdTask.entity) ifFailureThen applicationError
      _         <- taskEventPublisher.publish(createdTask.event) ifFailureThen applicationError
    } yield savedTask
  }

  def assignToTask(taskId: TaskId, user: User): Either[ApplicationError, Task] = {
    import crossroad0201.dddonscala.domain.task._

    for {
      task         <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
      assignedTask <- user.assignTo(task) ifLeftThen applicationError
      // FIXME 書き方はお好みで
      //assignedTask1 <- { user assignTo task } ifLeftThen applicationError
      //assignedTask2 <- (user assignTo task) ifLeftThen applicationError
      savedTask <- taskRepository.save(assignedTask.entity) ifFailureThen applicationError
      _         <- taskEventPublisher.publish(assignedTask.event) ifFailureThen applicationError
    } yield savedTask
  }

  def unAssignFromTask(taskId: TaskId): Either[ApplicationError, Task] = {
    for {
      task <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
      unAssignedTask <- task match {
        case t: AssignedTask =>
          for {
            unAssignedTask <- t.unAssign ifLeftThen applicationError
            savedTask      <- taskRepository.save(unAssignedTask.entity) ifFailureThen applicationError
            _              <- taskEventPublisher.publish(unAssignedTask.event) ifFailureThen applicationError
          } yield savedTask
        case t => Right(t)
      }
    } yield unAssignedTask
  }

  def commentToTask(taskId: TaskId, user: User, message: CommentMessage): Either[ApplicationError, Task] = {
    import crossroad0201.dddonscala.domain.task._

    for {
      task <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
      commentedTask = user commentTo (task, message)
      savedTask <- taskRepository.save(commentedTask.entity) ifFailureThen applicationError
      _         <- taskEventPublisher.publish(commentedTask.event) ifFailureThen applicationError
    } yield savedTask
  }

  def closeTask(taskId: TaskId): Either[ApplicationError, Task] = {
    for {
      task       <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
      closedTask <- task.close ifLeftThen applicationError
      savedTask  <- taskRepository.save(closedTask.entity) ifFailureThen applicationError
      _          <- taskEventPublisher.publish(closedTask.event) ifFailureThen applicationError
    } yield savedTask
  }

  def reOpenTask(taskId: TaskId): Either[ApplicationError, Task] = {
    for {
      task         <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
      reOpenedTask <- task.reOpen ifLeftThen applicationError
      savedTask    <- taskRepository.save(reOpenedTask.entity) ifFailureThen applicationError
      _            <- taskEventPublisher.publish(reOpenedTask.event) ifFailureThen applicationError
    } yield savedTask
  }

}

case class InvalidTaskOperationError(taskId: TaskId) extends BusinessError("error.invalidTaskOperation", taskId)
