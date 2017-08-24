package crossroad0201.dddonscala.infrastructure.task

import crossroad0201.dddonscala.domain.UnitOfWork
import crossroad0201.dddonscala.domain.task._
import crossroad0201.dddonscala.domain.user.UserId
import crossroad0201.dddonscala.infrastructure.rdb.ScalikeJdbcSessionHolder
import org.scalatest.{BeforeAndAfterAll, GivenWhenThen, Matchers}
import org.scalatest.fixture.WordSpec
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc._
import scalikejdbc.config.DBs

class TaskRepositoryOnRDBSpec
    extends WordSpec
    with GivenWhenThen
    with Matchers
    with BeforeAndAfterAll
    with AutoRollback {

  override protected def beforeAll() = DBs.setupAll

  override protected def afterAll() = DBs.closeAll

  override def fixture(implicit session: DBSession) {
    sql"""INSERT INTO tasks value ('TESTTASK001', 'テストタスク１', 'OPENED', 'USER001', NULL)""".update.apply

    sql"""INSERT INTO tasks value ('TESTTASK002', 'テストタスク２', 'CLOSED', 'USER001', 'USER002')""".update.apply
    sql"""INSERT INTO task_comments value (1, 'TESTTASK002', 'ひとつめのコメント', 'USER001')""".update.apply
    sql"""INSERT INTO task_comments value (2, 'TESTTASK002', 'ふたつめのコメント', 'USER002')""".update.apply
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
          actual should be('success)
          actual.get should not be empty
          actual.get.get should have(
            'id (TaskId("TESTTASK002")),
            'name (TaskName("テストタスク２")),
            'state (TaskState.Closed),
            'authorId (UserId("USER001")),
            'assignment (Assigned(UserId("USER002")))
          )
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
