package crossroad0201.dddonscala.query.taskview

/*
 * NOTE: 複数の集約をまたぐ（JOINした）ビューを提供するには、CQRSのクエリモデルを定義します。
 *
 * ここでは、タスク集約の情報に、ユーザー集約の情報（ユーザー名）をJOINし、
 * タスク名 や ユーザー名 でキーワード検索できるビューを想定しています。
 */

case class TaskView(
    taskId:       String,
    taskName:     String,
    taskState:    String,
    authorName:   String,
    assigneeName: Option[String],
    commentSize:  Int
)

case class TaskSearchResult(
    hits:  Int,
    items: Seq[TaskView]
)
