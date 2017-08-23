package crossroad0201.dddonscala.application

import crossroad0201.dddonscala.domain.EntityIdGenerator
import crossroad0201.dddonscala.domain.task.{
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

trait TaskService {
  implicit val entityIdGenerator: EntityIdGenerator
  val taskRepository:             TaskRepository
  val taskEventPublisher:         TaskEventPublisher

  // FIXME 実行ユーザーを implicit で
  // FIXME Unit of Work（DBコミット後に、イベントのパブリッシュする）

  private implicit val taskAlreadyClosedHandler: TaskAlreadyClosed => ServiceError = (e) =>
    IllegalTaskOperationError(e.task)
  private implicit val taskAlreadyOpenedHandler: TaskAlreadyOpened => ServiceError = (e) =>
    IllegalTaskOperationError(e.task)

  def createNewTask(name: TaskName, user: User): Either[ServiceError, Task] = {
    import crossroad0201.dddonscala.domain.task._

    val createdTask = user createTask name
    for {
      savedTask <- taskRepository.save(createdTask.entity) ifFailureThen asServiceError
      _         <- taskEventPublisher.publish(createdTask.event) ifFailureThen asServiceError
    } yield savedTask
  }

  def assignToTask(taskId: TaskId, user: User): Either[ServiceError, Task] = {
    import crossroad0201.dddonscala.domain.task._

    for {
      task         <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
      assignedTask <- user.assignTo(task) ifLeftThen asServiceError
      // FIXME 書き方はお好みで
      //assignedTask1 <- { user assignTo task } ifLeftThen asSserviceError
      //assignedTask2 <- (user assignTo task) ifLeftThen asServiceError
      savedTask <- taskRepository.save(assignedTask.entity) ifFailureThen asServiceError
      _         <- taskEventPublisher.publish(assignedTask.event) ifFailureThen asServiceError
    } yield savedTask
  }

  def unAssignFromTask(taskId: TaskId): Either[ServiceError, Task] = {
    for {
      task           <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
      unAssignedTask <- task.unAssign ifLeftThen asServiceError
      savedTask      <- taskRepository.save(unAssignedTask.entity) ifFailureThen asServiceError
      _              <- taskEventPublisher.publish(unAssignedTask.event) ifFailureThen asServiceError
    } yield savedTask
  }

  def commentToTask(taskId: TaskId, user: User, message: CommentMessage): Either[ServiceError, Task] = {
    import crossroad0201.dddonscala.domain.task._

    for {
      task <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
      commentedTask = user commentTo (task, message)
      savedTask <- taskRepository.save(commentedTask.entity) ifFailureThen asServiceError
      _         <- taskEventPublisher.publish(commentedTask.event) ifFailureThen asServiceError
    } yield savedTask
  }

  def closeTask(taskId: TaskId): Either[ServiceError, Task] = {
    for {
      task       <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
      closedTask <- task.close ifLeftThen asServiceError
      savedTask  <- taskRepository.save(closedTask.entity) ifFailureThen asServiceError
      _          <- taskEventPublisher.publish(closedTask.event) ifFailureThen asServiceError
    } yield savedTask
  }

  def reOpenTask(taskId: TaskId): Either[ServiceError, Task] = {
    for {
      task         <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
      reOpenedTask <- task.reOpen ifLeftThen asServiceError
      savedTask    <- taskRepository.save(reOpenedTask.entity) ifFailureThen asServiceError
      _            <- taskEventPublisher.publish(reOpenedTask.event) ifFailureThen asServiceError
    } yield savedTask
  }

}

case class IllegalTaskOperationError(task: Task)
    extends ApplicationError("error.invalidTaskOperation", task.id, task.state)
