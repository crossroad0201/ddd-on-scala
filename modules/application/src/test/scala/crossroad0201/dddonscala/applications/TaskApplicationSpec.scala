package crossroad0201.dddonscala.applications

import crossroad0201.dddonscala.UUIDEntityIdGenerator
import crossroad0201.dddonscala.domain.task
import crossroad0201.dddonscala.domain.task.{
  CommentMessage,
  Task,
  TaskEvent,
  TaskEventPublisher,
  TaskId,
  TaskName,
  TaskRepository
}
import crossroad0201.dddonscala.domain.user.{User, UserId}
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

import scala.collection.mutable
import scala.util.Success

class TaskApplicationSpec extends FeatureSpec with GivenWhenThen with Matchers {

  feature("Sandbox") {
    scenario("Create task and assign user") {
      new WithFixture {
        val actual = for {
          createdTask   <- createNewTask(TaskName("Test task"), User(UserId("CREATOR")))
          commentedTask <- commentToTask(createdTask.id, User(UserId("COMMENTER")), CommentMessage("Comment 1"))
          assignedTask  <- assignToTask(commentedTask.id, User(UserId("ASSIGNER1")))
          closedTask    <- closeTask(assignedTask.id)

          _ <- assignToTask(closedTask.id, User(UserId("ASSIGNER1")))

          reOpenedTask   <- reOpenTask(closedTask.id)
          commentedTask2 <- commentToTask(reOpenedTask.id, User(UserId("COMMENTER")), CommentMessage("Comment 2"))
          unAssignedTask <- unAssignFromTask(commentedTask2.id)
          assignedTask2  <- assignToTask(unAssignedTask.id, User(UserId("ASSIGNER2")))
          closedTask2    <- closeTask(assignedTask2.id)
        } yield closedTask2

        println(actual)
      }
    }
  }

  trait WithFixture extends TaskApplication {
    override implicit val entityIdGenerator = UUIDEntityIdGenerator

    override val taskRepository = new TaskRepository {
      val entities = mutable.Map[TaskId, Task]()
      override def get(id: task.TaskId) = {
        println(s"TaskRepository#get($id)")
        Success(entities.get(id))
      }
      override def save[T <: Task](task: T) = {
        println(s"TaskRepository#save($task)")
        entities.put(task.id, task)
        Success(task)
      }
    }

    override val taskEventPublisher = new TaskEventPublisher {
      override def publish[EVENT <: TaskEvent](event: EVENT) = {
        println(s"TaskEventPublisher#publish($event)")
        Success(event)
      }
    }
  }
}
