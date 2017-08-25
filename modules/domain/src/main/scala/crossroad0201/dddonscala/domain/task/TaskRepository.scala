package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.UnitOfWork

import scala.util.Try

trait TaskRepository {

  // FIXME Option[Task]に名前つける？
  def get(id: TaskId)(implicit uof: UnitOfWork): Try[Option[Task]]

  def save(task: Task)(implicit uof: UnitOfWork): Try[Task]

}
