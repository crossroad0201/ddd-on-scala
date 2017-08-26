package crossroad0201.dddonscala.query.taskview

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
