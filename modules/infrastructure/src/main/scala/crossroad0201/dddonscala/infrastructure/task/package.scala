package crossroad0201.dddonscala.infrastructure

import crossroad0201.dddonscala.domain.task.{Assigned, Assignment, CommentMessage, TaskId, TaskName, TaskState}
import crossroad0201.dddonscala.domain.user.UserId

package object task {

  implicit val toTaskId:   String => TaskId    = (v) => TaskId(v)
  implicit val toTaskName: String => TaskName  = (v) => TaskName(v)
  implicit val toTaskSate: String => TaskState = (v) => TaskState.valueOf(v)
  implicit val toAssignment: Option[String] => Assignment = {
    case Some(s) => Assigned(UserId(s))
    case None    => Assignment.notAssigned
  }
  implicit val toCommentMessage: String => CommentMessage = (v) => CommentMessage(v)

}
