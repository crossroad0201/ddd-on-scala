package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.DomainError

sealed trait TaskError extends DomainError {
  val task: Task
}
case class TaskAlreadyOpened(task: Task) extends TaskError
case class TaskAlreadyClosed(task: Task) extends TaskError
