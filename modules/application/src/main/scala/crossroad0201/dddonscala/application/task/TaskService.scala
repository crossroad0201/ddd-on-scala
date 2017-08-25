package crossroad0201.dddonscala.application.task

import crossroad0201.dddonscala.application._
import crossroad0201.dddonscala.domain.{EntityIdGenerator, EntityMetaDataCreator}
import crossroad0201.dddonscala.domain.task._
import crossroad0201.dddonscala.domain.user.{User, UserId}

import scala.language.postfixOps

trait TaskService extends TransactionAware {
  implicit val entityIdGenerator:     EntityIdGenerator
  implicit val entityMetaDataCreator: EntityMetaDataCreator

  val taskRepository:     TaskRepository
  val taskEventPublisher: TaskEventPublisher

  implicit val infraErrorHandler: Throwable => ServiceError
  private implicit val taskAlreadyClosedHandler: TaskAlreadyClosed => ServiceError = (e) =>
    IllegalTaskOperationError(e.task)
  private implicit val taskAlreadyOpenedHandler: TaskAlreadyOpened => ServiceError = (e) =>
    IllegalTaskOperationError(e.task)

  def createNewTask(name: TaskName, authorId: UserId): Either[ServiceError, Task] =
    tx { implicit uof =>
      import crossroad0201.dddonscala.domain.task._

      val author      = User(authorId, null) // FIXME Userエンティティを再構築する
      val createdTask = author.createTask(name)
      for {
        savedTask <- taskRepository.save(createdTask.entity) ifFailureThen asServiceError
        _         <- taskEventPublisher.publish(createdTask.event) ifFailureThen asServiceError
      } yield savedTask
    }

  def assignToTask(taskId: TaskId, assigneeId: UserId): Either[ServiceError, Task] =
    tx { implicit uof =>
      import crossroad0201.dddonscala.domain.task._

      val assignee = User(assigneeId, null) // FIXME Userエンティティを再構築する
      for {
        task         <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
        assignedTask <- assignee.assignTo(task) ifLeftThen asServiceError
        // FIXME 書き方はお好みで
        //assignedTask1 <- { user assignTo task } ifLeftThen asSserviceError
        //assignedTask2 <- (user assignTo task) ifLeftThen asServiceError
        savedTask <- taskRepository.save(assignedTask.entity) ifFailureThen asServiceError
        _         <- taskEventPublisher.publish(assignedTask.event) ifFailureThen asServiceError
      } yield savedTask
    }

  def unAssignFromTask(taskId: TaskId): Either[ServiceError, Task] = tx { implicit uof =>
    for {
      task           <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
      unAssignedTask <- task.unAssign ifLeftThen asServiceError
      savedTask      <- taskRepository.save(unAssignedTask.entity) ifFailureThen asServiceError
      _              <- taskEventPublisher.publish(unAssignedTask.event) ifFailureThen asServiceError
    } yield savedTask
  }

  def commentToTask(taskId: TaskId, commenterId: UserId, message: CommentMessage): Either[ServiceError, Task] =
    tx { implicit uof =>
      import crossroad0201.dddonscala.domain.task._

      val commenter = User(commenterId, null) // FIXME Userエンティティを再構築する
      for {
        task <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
        commentedTask = commenter.commentTo(task, message)
        savedTask <- taskRepository.save(commentedTask.entity) ifFailureThen asServiceError
        _         <- taskEventPublisher.publish(commentedTask.event) ifFailureThen asServiceError
      } yield savedTask
    }

  def closeTask(taskId: TaskId): Either[ServiceError, Task] =
    tx { implicit uof =>
      for {
        task       <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
        closedTask <- task.close ifLeftThen asServiceError
        savedTask  <- taskRepository.save(closedTask.entity) ifFailureThen asServiceError
        _          <- taskEventPublisher.publish(closedTask.event) ifFailureThen asServiceError
      } yield savedTask
    }

  def reOpenTask(taskId: TaskId): Either[ServiceError, Task] =
    tx { implicit uof =>
      for {
        task         <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
        reOpenedTask <- task.reOpen ifLeftThen asServiceError
        savedTask    <- taskRepository.save(reOpenedTask.entity) ifFailureThen asServiceError
        _            <- taskEventPublisher.publish(reOpenedTask.event) ifFailureThen asServiceError
      } yield savedTask
    }

}

case class IllegalTaskOperationError(task: Task)
    extends ApplicationError(ServiceErrorCodes.InvalidTaskOperation, task.id, task.state)
