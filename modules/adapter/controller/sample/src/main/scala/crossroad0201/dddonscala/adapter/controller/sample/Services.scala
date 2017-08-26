package crossroad0201.dddonscala.adapter.controller.sample

import crossroad0201.dddonscala.adapter.infrastructure.rdb.task.TaskRepositoryOnRDB
import crossroad0201.dddonscala.application.task.TaskService
import crossroad0201.dddonscala.domain.task.{TaskEvent, TaskEventPublisher, TaskRepository}
import crossroad0201.dddonscala.domain.user.UserRepository
import crossroad0201.dddonscala.domain.{UnitOfWork, user}

object Services {

  val TaskService = new TaskService with InfrastructureAware {
    override val taskRepository = new TaskRepository with TaskRepositoryOnRDB
    override val taskEventPublisher = new TaskEventPublisher {
      override def publish[EVENT <: TaskEvent](event: EVENT)(implicit uow: UnitOfWork) = ??? // FIXME
    }
    override val userRepository = new UserRepository {
      override def get(id: user.UserId)(implicit uow: UnitOfWork) = ??? // FIXME
    }
  }

}
