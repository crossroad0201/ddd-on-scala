package crossroad0201.dddonscala.adapter.controller.sample

import crossroad0201.dddonscala.application.task.TaskService
import crossroad0201.dddonscala.infrastructure.task._
import crossroad0201.dddonscala.infrastructure.user._

trait SampleController {
  val taskService: TaskService

  def createTask(taskName: String, authorId: String): Option[String] = {
    (for {
      createdTask <- taskService.createNewTask(taskName, authorId)
    } yield createdTask) fold (
      error => {
        println(s"$error") // TODO エラーコードからエラーメッセージを作る
        None
      },
      task => {
        println(s"タスク ${task.name} が作成されました。 ID = ${task.id.value}")
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

object SampleControllerImpl extends SampleController {
  override val taskService = Services.TaskService
}
