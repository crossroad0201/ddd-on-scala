package crossroad0201.dddonscala.adapter.sample

import crossroad0201.dddonscala.application.task.TaskService
import crossroad0201.dddonscala.domain.{user, UnitOfWork}
import crossroad0201.dddonscala.domain.task.{TaskEvent, TaskEventPublisher, TaskRepository}
import crossroad0201.dddonscala.domain.user.UserRepository
import crossroad0201.dddonscala.infrastructure._
import crossroad0201.dddonscala.infrastructure.task.{TaskRepositoryOnRDB, _}
import crossroad0201.dddonscala.infrastructure.user._

trait SampleAdapter {
  val taskService: TaskService

  def createTask(taskName: String, authorId: String): Option[String] = {
    (for {
      createdTask <- taskService.createNewTask(taskName, authorId)
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
      closedTask <- taskService.closeTask(taskId)
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
  override val taskService = new TaskService with InfrastructureAware {
    override val taskRepository = new TaskRepository with TaskRepositoryOnRDB
    override val taskEventPublisher = new TaskEventPublisher {
      override def publish[EVENT <: TaskEvent](event: EVENT)(implicit uow: UnitOfWork) = ??? // FIXME
    }
    override val userRepository = new UserRepository {
      override def get(id: user.UserId)(implicit uow: UnitOfWork) = ??? // FIXME
    }
  }
}
