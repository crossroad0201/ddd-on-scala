package crossroad0201.dddonscala.adapter.controller.sample

import crossroad0201.dddonscala.application.task.TaskService
import crossroad0201.dddonscala.infrastructure.task._
import crossroad0201.dddonscala.infrastructure.user._
import crossroad0201.dddonscala.query.taskview.TaskViewQueryProcessor

trait SampleController {
  val taskService:            TaskService
  val taskViewQueryProcessor: TaskViewQueryProcessor

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

  def searchTask(keyword: String): Seq[String] = {
    (for {
      searchResult <- taskViewQueryProcessor.searchTasks(keyword)
    } yield searchResult) fold (
      error => {
        println(s"$error")
        Nil
      },
      result => {
        println(s"${result.hits} 件のタスクが見つかりました。")
        result.items.zipWithIndex.foreach {
          case (item, index) =>
            println(
              s"  $index: ${item.taskName} ${item.taskState} ${item.authorName} ${item.assigneeName} ${item.commentSize}")
        }
        result.items.map(_.taskId)
      }
    )
  }

}

object SampleControllerImpl extends SampleController {
  override val taskService            = Components.TaskService
  override val taskViewQueryProcessor = Components.TaskViewQueryProcessor
}
