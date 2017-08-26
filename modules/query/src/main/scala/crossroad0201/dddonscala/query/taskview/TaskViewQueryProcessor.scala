package crossroad0201.dddonscala.query.taskview

import scala.util.Try

trait TaskViewQueryProcessor {

  def searchTasks(keyword: String): Try[TaskSearchResult]

}
