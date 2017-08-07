package crossroad0201.dddonscala.applications

import crossroad0201.dddonscala.UUIDEntityIdGenerator
import crossroad0201.dddonscala.domain.task
import crossroad0201.dddonscala.domain.task.{ CommentMessage, Task, TaskAssigned, TaskCommented, TaskCreated, TaskEventPublisher, TaskId, TaskName, TaskRepository, UnAssignedTask }
import crossroad0201.dddonscala.domain.user.{ User, UserId }
import org.scalatest.{ FeatureSpec, GivenWhenThen, Matchers }

import scala.collection.mutable
import scala.util.Success

class TaskApplicationSpec extends FeatureSpec with GivenWhenThen with Matchers {

  feature("Sandbox") {
    scenario("Create task and assign user") {
      new WithFixture {
        val user = User(UserId("WHO"))

        val actual = for {
          newTask <- createNewTask(TaskName("HOGEHOGE"), user)
          assignedTask <- assignToTask(newTask.id, user)
          commentedTask1 <- commentToTask(assignedTask.id, CommentMessage("This is a first comment."), user)
          commentedTask2 <- commentToTask(commentedTask1.id, CommentMessage("This is a second comment."), user)
        } yield commentedTask2

        println(actual)
      }
    }
  }

  trait WithFixture extends TaskApplication {
    override implicit val entityIdGenerator = UUIDEntityIdGenerator

    override val taskRepository = new TaskRepository {
      val entities = mutable.Map[TaskId, Task]()
      override def get(id: task.TaskId) = {
        println(s"TaskRepository#get(${id})")
        Success(entities.get(id))
      }
      override def save[T <: Task](task: T) = {
        println(s"TaskRepository#save($task)")
        entities.put(task.id, task)
        Success(task)
      }
    }

    override val taskEventPublisher = new TaskEventPublisher {
      override def publish(event: TaskCreated) = {
        println(s"TaskEventPublisher#publish($event)")
        Success(event)
      }
      override def publish(event: TaskAssigned) = {
        println(s"TaskEventPublisher#publish($event)")
        Success(event)
      }
      override def publish(event: TaskCommented) = {
        println(s"TaskEventPublisher#publish($event)")
        Success(event)
      }
    }
  }
}
