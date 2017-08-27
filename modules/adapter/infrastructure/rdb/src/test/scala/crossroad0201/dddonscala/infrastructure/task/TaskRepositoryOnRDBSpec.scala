package crossroad0201.dddonscala.infrastructure.task

import crossroad0201.dddonscala.adapter.infrastructure.rdb.ScalikeJdbcSessionHolder
import crossroad0201.dddonscala.adapter.infrastructure.rdb.task.TaskRepositoryOnRDB
import crossroad0201.dddonscala.domain.UnitOfWork
import crossroad0201.dddonscala.domain.task._
import crossroad0201.dddonscala.domain.user.UserId
import org.scalatest.{BeforeAndAfterAll, GivenWhenThen, Inside, Matchers}
import org.scalatest.fixture.WordSpec
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._
import scalikejdbc.config.DBs

import scala.util.{Failure, Success}

class TaskRepositoryOnRDBSpec
    extends WordSpec
    with GivenWhenThen
    with Matchers
    with Inside
    with BeforeAndAfterAll
    with AutoRollback {

  override protected def beforeAll() = DBs.setupAll

  override protected def afterAll() = DBs.closeAll

  override def fixture(implicit session: DBSession) {
    sql"""INSERT INTO tasks VALUES ('TESTTASK001', 'テストタスク１', 'OPENED', 'USER001', NULL, 1)""".update.apply

    sql"""INSERT INTO tasks VALUES ('TESTTASK002', 'テストタスク２', 'CLOSED', 'USER001', 'USER002', 1)""".update.apply
    sql"""INSERT INTO task_comments VALUES (1, 'TESTTASK002', 'ひとつめのコメント', 'USER001')""".update.apply
    sql"""INSERT INTO task_comments VALUES (2, 'TESTTASK002', 'ふたつめのコメント', 'USER002')""".update.apply
  }

  "get" when {
    "タスクが存在する" should {
      "タスクが返される" in { implicit dbs =>
        new WithFixture {
          Given("存在するタスクID")
          val taskId = "TESTTASK002"

          Then("タスクを取得する")
          val actual = get(TaskId(taskId))
          println(s"Actual: $actual")

          When("タスクが返される")
          inside(actual) {
            case (Success(Some(aTask))) =>
              aTask.id should be(TaskId("TESTTASK002"))
              aTask.name should be(TaskName("テストタスク２"))
              aTask.state should be(TaskState.Closed)
              aTask.authorId should be(UserId("USER001"))
              aTask.assignment should be(Assigned(UserId("USER002")))
          }
        }
      }
    }
  }

  trait WithFixture extends TaskRepositoryOnRDB {
    implicit def dbSessionAsUnitOfWork(implicit dbs: DBSession): UnitOfWork =
      new UnitOfWork with ScalikeJdbcSessionHolder {
        override val dbSession = dbs
      }
  }

}
