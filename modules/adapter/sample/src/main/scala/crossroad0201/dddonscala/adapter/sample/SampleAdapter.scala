package crossroad0201.dddonscala.adapter.sample

import crossroad0201.dddonscala.UUIDEntityIdGenerator
import crossroad0201.dddonscala.application.task.TaskService
import crossroad0201.dddonscala.domain.UnitOfWork
import crossroad0201.dddonscala.domain.task.{TaskEvent, TaskEventPublisher, TaskId, TaskName, TaskRepository}
import crossroad0201.dddonscala.domain.user.{User, UserId}
import crossroad0201.dddonscala.infrastructure.TransactionAwareImpl
import crossroad0201.dddonscala.infrastructure.task.TaskRepositoryOnRDB

trait SampleAdapter {
  val taskService: TaskService

  def createTask(taskName: String): Option[String] = {
    (for {
      createdTask <- taskService.createNewTask(TaskName(taskName), User(UserId("USER001")))
    } yield createdTask) fold (
      error => {
        println(s"$error") // FIXME エラーコードからエラーメッセージを作る
        None
      },
      task => {
        println(s"タスク ${task.name} が作成されました。 ID = ${task.id.value}") // FIXME VOをプリミティブ値にimplicit展開
        Some(task.id.value)
      }
    )
  }

  def closeTask(taskId: String): Option[String] = {
    (for {
      closedTask <- taskService.closeTask(TaskId(taskId))
    } yield closedTask) fold (
      error => {
        println(s"$error")
        None
      },
      task => {
        println(s"タスク ${task.name} がクローズされました。 ID = ${task.id.value}")
        Some(task.id.value)
      }
    )
  }

}

object SampleAdapterImpl extends SampleAdapter {
  override val taskService = new TaskService with TransactionAwareImpl {
    override implicit val entityIdGenerator = UUIDEntityIdGenerator
    override val taskRepository             = new TaskRepository with TaskRepositoryOnRDB
    override val taskEventPublisher = new TaskEventPublisher {
      override def publish[EVENT <: TaskEvent](event: EVENT)(implicit uow: UnitOfWork) = ??? // FIXME
    }
  }
}
