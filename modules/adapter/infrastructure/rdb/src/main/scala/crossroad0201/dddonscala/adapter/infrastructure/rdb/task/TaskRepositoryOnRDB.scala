package crossroad0201.dddonscala.adapter.infrastructure.rdb.task

import crossroad0201.dddonscala.adapter.infrastructure.rdb.ScalikeJdbcAware
import crossroad0201.dddonscala.domain.UnitOfWork
import crossroad0201.dddonscala.domain.task._
import crossroad0201.dddonscala.infrastructure._
import crossroad0201.dddonscala.infrastructure.task._
import crossroad0201.dddonscala.infrastructure.user._
import scalikejdbc._

import scala.util.Try

// FIXME インフラの実装はいらないんじゃないか？（逆にインフラがなくてもテストできるよ、とか）
trait TaskRepositoryOnRDB extends TaskRepository with ScalikeJdbcAware {

  override def get(id: TaskId)(implicit uof: UnitOfWork) = Try {
    def getTask: Option[Task] = {
      sql"""
        |SELECT
        |  task_id,
        |  name,
        |  state,
        |  author_id,
        |  assignee_id,
        |  version
        |FROM
        |  tasks
        |WHERE
        |  task_id = ${id.value}
      """.stripMargin.map { rs =>
        Task(
          id         = rs.string("task_id"),
          name       = rs.string("name"),
          state      = rs.string("state"),
          authorId   = rs.string("author_id"),
          assignment = rs.stringOpt("assignee_id"),
          metaData   = rs.int("version")
        )
      }.single.apply
    }

    def getComments: Comments = {
      sql"""
        |SELECT
        |  message,
        |  commenter_id
        |FROM
        |  task_comments
        |WHERE
        |  task_id = ${id.value}
        |ORDER BY
        |  id
      """.stripMargin.map { rs =>
        Comment(
          message     = rs.string("message"),
          commenterId = rs.string("commenter_id")
        )
      }.list.apply.foldLeft(Comments.nothing) { (comments, comment) =>
        comments.add(comment)
      }
    }

    for {
      maybeTask <- getTask
    } yield maybeTask.copy(comments = getComments)
  }

  override def save(task: Task)(implicit uof: UnitOfWork) = ??? // FIXME

  override def delete(task: Task)(implicit uof: UnitOfWork) = ??? // FIXME

}
