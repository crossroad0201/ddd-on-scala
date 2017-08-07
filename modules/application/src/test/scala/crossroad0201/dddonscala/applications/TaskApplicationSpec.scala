package crossroad0201.dddonscala.applications

import crossroad0201.dddonscala.domain.task.{ Task, TaskName, TaskRepository, UnAssignedTask }
import crossroad0201.dddonscala.domain.user.{ User, UserId }
import crossroad0201.dddonscala.domain.{ DomainEvent, EventMarshaller, EventPublisher, task }
import crossroad0201.dddonscala.infrastructure.{ JSON, UUIDEntityIdGenerator }
import org.scalatest.{ FeatureSpec, GivenWhenThen, Matchers }

import scala.util.{ Success, Try }

class TaskApplicationSpec extends FeatureSpec with GivenWhenThen with Matchers {

  feature("Sandbox") {
    scenario("Create task and assign user") {
      val sut = new TaskApplication {
        override implicit val entityIdGenerator = UUIDEntityIdGenerator
        override val taskRepository = new TaskRepository {
          override def get(id: task.TaskId) = Try(Some(UnAssignedTask(id, TaskName("HOGEHOGE"), UserId("WHO"))))

          override def save[T <: Task](task: T) = Try(task)
        }
        override val eventPublisher = new EventPublisher {
          override type Format = JSON
          override def publish[EVENT <: DomainEvent, JSON](event: EVENT)(implicit marshaller: EventMarshaller[EVENT, JSON]) = {
            for {
              marshaled <- marshaller.marshal(event)
            } yield print(marshaled)
            Success(event)
          }
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
