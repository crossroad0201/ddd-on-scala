package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.{repository, UnitOfWork}

import scala.util.Try

@repository
trait TaskRepository {

  def get(id: TaskId)(implicit uof: UnitOfWork): Try[Option[Task]]

  def save(task: Task)(implicit uof: UnitOfWork): Try[Task]

}
