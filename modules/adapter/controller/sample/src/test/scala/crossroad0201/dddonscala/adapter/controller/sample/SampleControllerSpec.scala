package crossroad0201.dddonscala.adapter.controller.sample

import crossroad0201.dddonscala.application.task.TaskService
import crossroad0201.dddonscala.domain.task.{
  Assignment,
  Comments,
  Task,
  TaskClosed,
  TaskCreated,
  TaskEventPublisher,
  TaskId,
  TaskName,
  TaskRepository,
  TaskState
}
import crossroad0201.dddonscala.domain.user.{User, UserId, UserRepository}
import crossroad0201.dddonscala.domain.{EntityIdGenerator, UnitOfWork}
import crossroad0201.dddonscala.infrastructure.EntityMetaDataImpl
import crossroad0201.dddonscala.infrastructure.task._
import crossroad0201.dddonscala.infrastructure.user._
import crossroad0201.dddonscala.query.taskview.{TaskSearchResult, TaskView, TaskViewQueryProcessor}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

import scala.util.Success

class SampleControllerSpec extends FeatureSpec with GivenWhenThen with Matchers with MockFactory {

  feature("タスクを新規作成できる") {
    scenario("タスク名と作成者を指定して、タスクを新規作成する") {
      new WithFixture {
        Given("作成者が存在する")
        (mockUserRepository
          .get(_: UserId)(_: UnitOfWork))
          .expects(UserId("USER001"), *)
          .onCall { (aId, _) =>
            Success(Some {
              User(aId, "テストユーザー１", EntityMetaDataImpl(1))
            })
          }
          .once

        Then("タスクが保存される")
        (mockTaskRepository
          .save(_: Task)(_: UnitOfWork))
          .expects(where {
            case (aTask, _) =>
              aTask should have(
                'id (TaskId("1")),
                'name (TaskName("テストタスク")),
                'state (TaskState.Opened),
                'authorId (UserId("USER001")),
                'assignment (Assignment.notAssigned),
                'comments (Comments.nothing)
              )
              true
          })
          .onCall { (aTask, _) =>
            Success(aTask)
          }
          .once

        Then("タスク作成イベントが発行される")
        (mockTaskEventPublisher
          .publish(_: TaskCreated)(_: UnitOfWork))
          .expects(where {
            case (aEvent, _) =>
              aEvent should have(
                'taskId (TaskId("1")),
                'name (TaskName("テストタスク")),
                'authorId (UserId("USER001"))
              )
              true
          })
          .onCall { (aEvent, _) =>
            Success(aEvent)
          }
          .once

        When("タスクを作成する")
        val actual = sut.createTask("テストタスク", "USER001")

        Then("作成されたタスクのIDが返される")
        actual should contain("1")
      }
    }
  }

  feature("タスクをクローズできる") {
    scenario("存在するタスクを指定してクローズする") {
      new WithFixture {
        Given("タスクが存在する")
        (mockTaskRepository
          .get(_: TaskId)(_: UnitOfWork))
          .expects(TaskId("123"), *)
          .onCall { (aId, _) =>
            Success(Some {
              Task(
                id       = aId,
                name     = "テストタスク",
                authorId = "USER001",
                metaData = EntityMetaDataImpl(1)
              )
            })
          }
          .once

        Then("タスクの状態がクローズに更新される")
        (mockTaskRepository
          .save(_: Task)(_: UnitOfWork))
          .expects(where {
            case (aTask, _) =>
              aTask should have(
                'id (TaskId("123")),
                'state (TaskState.Closed)
              )
              true
          })
          .onCall { (aTask, _) =>
            Success(aTask)
          }
          .once

        Then("タスククローズイベントが発行される")
        (mockTaskEventPublisher
          .publish(_: TaskClosed)(_: UnitOfWork))
          .expects(where {
            case (aEvent, _) =>
              aEvent should have(
                'taskId (TaskId("123"))
              )
              true
          })
          .onCall { (aEvent, _) =>
            Success(aEvent)
          }
          .once

        When("タスクをクローズする")
        val actual = sut.closeTask("123")

        Then("クローズされたタスクのIDが返される")
        actual should contain("123")
      }
    }
  }

  feature("タスクを検索できる") {
    scenario("タスク名や作成者名で検索する") {
      new WithFixture {
        (mockTaskViewQueryProcessor
          .searchTasks(_: String))
          .expects("DDD")
          .returning {
            Success {
              TaskSearchResult(
                hits = 3,
                items = Seq(
                  TaskView(
                    taskId       = "1",
                    taskName     = "DDDをScalaで実践する",
                    taskState    = "CLOSED",
                    authorName   = "テストユーザー１",
                    assigneeName = Some("テストユーザー１"),
                    commentSize  = 3
                  ),
                  TaskView(
                    taskId       = "3",
                    taskName     = "サンプルプログラムを実装する",
                    taskState    = "CLOSED",
                    authorName   = "DDD好きのユーザー",
                    assigneeName = Some("テストユーザー２"),
                    commentSize  = 10
                  ),
                  TaskView(
                    taskId       = "5",
                    taskName     = "READMEにDDDの解説を書く",
                    taskState    = "OPENED",
                    authorName   = "テストユーザー２",
                    assigneeName = Some("テストユーザー１"),
                    commentSize  = 1
                  )
                )
              )
            }
          }
          .once

        When("タスクをキーワードで検索する")
        val actual = sut.searchTask("DDD")

        Then("ヒットしたタスクのIDのリストが返される")
        actual should contain only ("1", "3", "5")
      }
    }
  }

  trait WithFixture {
    val mockTaskRepository         = mock[TaskRepository]
    val mockTaskEventPublisher     = mock[TaskEventPublisher]
    val mockUserRepository         = mock[UserRepository]
    val mockTaskViewQueryProcessor = mock[TaskViewQueryProcessor]

    val sut = new SampleController {
      override val taskService = new TaskService with InfrastructureAware {
        override implicit val entityIdGenerator = new EntityIdGenerator {
          var currentId: Int = 0
          override def genId() = {
            currentId = 1
            currentId.toString
          }
        }
        override def tx[A](f:         (UnitOfWork) => A) = f(new UnitOfWork {})
        override def txReadonly[A](f: (UnitOfWork) => A) = f(new UnitOfWork {})

        override val taskRepository     = mockTaskRepository
        override val taskEventPublisher = mockTaskEventPublisher
        override val userRepository     = mockUserRepository
      }

      override val taskViewQueryProcessor = mockTaskViewQueryProcessor
    }
  }
}
