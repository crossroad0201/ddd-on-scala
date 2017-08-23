package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.user.UserId
import crossroad0201.dddonscala.domain.valueobject

@valueobject
case class Comment(message: CommentMessage, commenterId: UserId)

@valueobject
case class Comments(comments: Seq[Comment]) {
  def add(comment: Comment): Comments = copy(comments = comments :+ comment)
}
object Comments {
  val Nothing = Comments(Seq())
}
