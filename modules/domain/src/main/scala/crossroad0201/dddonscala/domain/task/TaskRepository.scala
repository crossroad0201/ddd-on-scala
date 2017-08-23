package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.repository

import scala.util.Try

// FIXME サンプル実装を作る
@repository
trait TaskRepository {

  def get(id: TaskId): Try[Option[Task]]

  def save[T <: Task](task: T): Try[T]

}
