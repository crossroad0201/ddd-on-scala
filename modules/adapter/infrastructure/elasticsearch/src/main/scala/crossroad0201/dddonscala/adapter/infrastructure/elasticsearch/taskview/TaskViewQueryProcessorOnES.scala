package crossroad0201.dddonscala.adapter.infrastructure.elasticsearch.taskview

import crossroad0201.dddonscala.query.taskview.TaskViewQueryProcessor

trait TaskViewQueryProcessorOnES extends TaskViewQueryProcessor {

  override def searchTasks(keyword: String) = throw new UnsupportedOperationException("このサンプルでは未実装です。")

}
