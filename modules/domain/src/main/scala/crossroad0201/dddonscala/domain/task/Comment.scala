package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.user.UserId

case class Comment(message: CommentMessage, commenterId: UserId)

// NOTE: 値の集合は素のコレクションのままでは扱わずに、集合を表す型（ファーストクラスコレクション）を定義します。

case class Comments(comments: Seq[Comment]) {
  def add(comment: Comment): Comments = copy(comments = comments :+ comment)
}
object Comments {
  val nothing = Comments(Seq())
}
