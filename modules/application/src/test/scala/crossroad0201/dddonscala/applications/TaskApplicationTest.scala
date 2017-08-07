package crossroad0201.dddonscala.applications

import crossroad0201.dddonscala.UUIDEntityIdGenerator
import crossroad0201.dddonscala.domain.task
import crossroad0201.dddonscala.domain.task.{ Task, TaskAssigned, TaskCreated, TaskEventPublisher, TaskName, TaskRepository, UnAssignedTask }
import crossroad0201.dddonscala.domain.user.{ User, UserId }
import org.scalatest.{ FeatureSpec, GivenWhenThen, Matchers }

import scala.util.Try

class TaskApplicationTest extends FeatureSpec with GivenWhenThen with Matchers {

  feature("Sandbox") {
    scenario("Create task and assign user") {
      val sut = new TaskApplication {
        override implicit val entityIdGenerator = UUIDEntityIdGenerator
        override val taskRepository = new TaskRepository {
          override def get(id: task.TaskId) = Try(Some(UnAssignedTask(id, TaskName("HOGEHOGE"), UserId("WHO"))))

          override def save[T <: Task](task: T) = Try(task)
        }
        override val taskEventPublisher = new TaskEventPublisher {
          override def publish(event: TaskCreated) = Try(event)

          override def publish(event: TaskAssigned) = Try(event)
        }
      }

      val user = User(UserId("WHO"))

      val actual = for {
        newTask <- sut.createNewTask(TaskName("HOGEHOGE"), user)
        assignedTask <- sut.assignToTask(newTask.id, user)
      } yield assignedTask

      println(actual)
    }
  }
}
