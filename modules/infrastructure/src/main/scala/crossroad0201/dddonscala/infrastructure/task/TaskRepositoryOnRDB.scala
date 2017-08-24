package crossroad0201.dddonscala.infrastructure.task

import crossroad0201.dddonscala.domain.UnitOfWork
import crossroad0201.dddonscala.domain.task._
import crossroad0201.dddonscala.domain.user.UserId
import crossroad0201.dddonscala.infrastructure.rdb.ScalikeJdbcAware
import scalikejdbc._

import scala.util.Try

trait TaskRepositoryOnRDB extends TaskRepository with ScalikeJdbcAware {

  override def get(id: TaskId)(implicit uof: UnitOfWork) = Try {
    def getTask: Option[Task] = {
      sql"""
        |SELECT
        |  task_id,
        |  name,
        |  state,
        |  author_id,
        |  assignee_id
        |FROM
        |  tasks
        |WHERE
        |  task_id = ${id.value}
      """.stripMargin.map { rs =>
        Task(
          id         = TaskId(rs.string("task_id")),
          name       = TaskName(rs.string("name")),
          state      = TaskState.valueOf(rs.string("state")),
          authorId   = UserId(rs.string("author_id")),
          assignment = rs.stringOpt("assignee_id").map(v => Assigned(UserId(v))).getOrElse(Assignment.notAssigned)
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
          message     = CommentMessage(rs.string("message")),
          commenterId = UserId(rs.string("commenter_id"))
        )
      }.list.apply.foldLeft(Comments.Nothing) { (comments, comment) =>
        comments.add(comment)
      }
    }

    for {
      maybeTask <- getTask
    } yield maybeTask.copy(comments = getComments)
  }

  // FIXME TaskRepo#save を実装する
  override def save[T <: Task](task: T)(implicit uof: UnitOfWork) = ???

}