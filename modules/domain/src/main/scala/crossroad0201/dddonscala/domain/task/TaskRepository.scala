package crossroad0201.dddonscala.domain.task

import scala.util.Try

trait TaskRepository {

  def get(id: TaskId): Try[Option[Task]]
  def save[T <: Task](task: T): Try[T]

}
