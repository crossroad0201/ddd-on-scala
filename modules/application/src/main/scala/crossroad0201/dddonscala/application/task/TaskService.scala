package crossroad0201.dddonscala.application.task

import crossroad0201.dddonscala.application._
import crossroad0201.dddonscala.domain.task._
import crossroad0201.dddonscala.domain.user.{UserId, UserRepository}
import crossroad0201.dddonscala.domain.{EntityIdGenerator, EntityMetaDataCreator}

import scala.language.postfixOps

trait TaskService extends TransactionAware {

  /*
   * NOTE: アプリケーションサービスは特定のインフラに依存しません。
   * インターフェースレイヤで、アプリケーションサービスにインフラを依存性注入します。
   */

  implicit val entityIdGenerator:     EntityIdGenerator
  implicit val entityMetaDataCreator: EntityMetaDataCreator

  val taskRepository:     TaskRepository
  val taskEventPublisher: TaskEventPublisher
  val userRepository:     UserRepository

  /*
   * NOTE: ドメインレイヤ／インフラレイヤで発生するエラーを、ユースケースのエラーに変換します。
   */

  implicit val infraErrorHandler: Throwable => ServiceError
  private implicit val taskAlreadyClosedHandler: TaskAlreadyClosed => ServiceError = (e) =>
    IllegalTaskOperationError(e.task)
  private implicit val taskAlreadyOpenedHandler: TaskAlreadyOpened => ServiceError = (e) =>
    IllegalTaskOperationError(e.task)

  /*
   * NOTE: アプリケーションサービスのメソッドの実装は、基本的に以下の処理ステップを for内方式 で包んだ形になります。
   *
   * 1. リポジトリを使って、処理対象のエンティティを取得する。
   * 2. エンティティのメソッド、およびドメインサービスを呼び出して、ユースケースの処理を行う。
   * 3. リポジトリを使って、処理したエンティティを永続化する。
   * 4. イベントパブリッシャーを使って、発生したドメインイベントを通知する。
   */

  /*
   * NOTE: アプリケーションサービスのメソッドの実装は、ユースケース記述のようになります。
   * ロジックを書くのではなく、ユースケース記述のように書くことで、コードが仕様を自己説明できるようになります。
   *
   * また、ドメインモデルのインターフェース（メソッド名など）は、アプリケーションサービスを
   * 自然な表現で記述できるように設計し、必要であれば implicit conversion などのScalaの言語機能を活用します。
   */

  def createNewTask(name: TaskName, authorId: UserId): Either[ServiceError, Task] =
    tx { implicit uow =>
      // NOTE: 他集約のエンティティを、自集約のロール型に変換するために、パッケージをimportします。
      import crossroad0201.dddonscala.domain.task.{TaskService => TaskDomainService, _}

      for {
        author <- userRepository.get(authorId) ifNotExists NotFoundError("USER", authorId)
        createdTask  = author.createTask(name)
        exampledTask = TaskDomainService.applyBusinessRuleTo(createdTask.entity)
        savedTask <- taskRepository.save(exampledTask) ifFailureThen asServiceError
        _         <- taskEventPublisher.publish(createdTask.event) ifFailureThen asServiceError
      } yield savedTask
    }

  def assignToTask(taskId: TaskId, assigneeId: UserId): Either[ServiceError, Task] =
    tx { implicit uow =>
      import crossroad0201.dddonscala.domain.task._

      for {
        task         <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
        assignee     <- userRepository.get(assigneeId) ifNotExists NotFoundError("USER", assigneeId)
        assignedTask <- assignee.assignTo(task) ifLeftThen asServiceError
        savedTask    <- taskRepository.save(assignedTask.entity) ifFailureThen asServiceError
        _            <- taskEventPublisher.publish(assignedTask.event) ifFailureThen asServiceError
      } yield savedTask
    }

  def unAssignFromTask(taskId: TaskId): Either[ServiceError, Task] =
    tx { implicit uow =>
      for {
        task           <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
        unAssignedTask <- task.unAssign ifLeftThen asServiceError
        savedTask      <- taskRepository.save(unAssignedTask.entity) ifFailureThen asServiceError
        _              <- taskEventPublisher.publish(unAssignedTask.event) ifFailureThen asServiceError
      } yield savedTask
    }

  def commentToTask(taskId: TaskId, commenterId: UserId, message: CommentMessage): Either[ServiceError, Task] =
    tx { implicit uow =>
      import crossroad0201.dddonscala.domain.task._

      for {
        task      <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
        commenter <- userRepository.get(commenterId) ifNotExists NotFoundError("USER", commenterId)
        commentedTask = commenter.commentTo(task, message)
        savedTask <- taskRepository.save(commentedTask.entity) ifFailureThen asServiceError
        _         <- taskEventPublisher.publish(commentedTask.event) ifFailureThen asServiceError
      } yield savedTask
    }

  def closeTask(taskId: TaskId): Either[ServiceError, Task] =
    tx { implicit uow =>
      for {
        task       <- taskRepository.get(taskId) ifNotExists NotFoundError("TASK", taskId)
        closedTask <- task.close ifLeftThen asServiceError
        savedTask  <- taskRepository.save(closedTask.entity) ifFailureThen asServiceError
        _          <- taskEventPublisher.publish(closedTask.event) ifFailureThen asServiceError
      } yield savedTask
    }

  def reOpenTask(taskId: TaskId): Either[ServiceError, Task] =
    tx { implicit uow =>
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
