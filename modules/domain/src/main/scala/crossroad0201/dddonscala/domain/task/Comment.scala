package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.user.UserId

case class Comment(message: CommentMessage, commenterId: UserId)

case class Comments(comments: Seq[Comment]) {
  def add(comment: Comment): Comments = copy(comments = comments :+ comment)
}
object Comments {
  val nothing = Comments(Seq())
}
