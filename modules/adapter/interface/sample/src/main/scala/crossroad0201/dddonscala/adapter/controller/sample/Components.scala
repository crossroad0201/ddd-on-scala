package crossroad0201.dddonscala.adapter.controller.sample

import crossroad0201.dddonscala.adapter.infrastructure.elasticsearch.taskview.TaskViewQueryProcessorOnES
import crossroad0201.dddonscala.adapter.infrastructure.kafka.task.TaskEventPublisherOnKafka
import crossroad0201.dddonscala.adapter.infrastructure.rdb.task.TaskRepositoryOnRDB
import crossroad0201.dddonscala.adapter.infrastructure.rdb.user.UserRepositoryOnRDB
import crossroad0201.dddonscala.application.task.TaskService
import crossroad0201.dddonscala.domain.task.{TaskEventPublisher, TaskRepository}
import crossroad0201.dddonscala.domain.user.UserRepository
import crossroad0201.dddonscala.query.taskview.TaskViewQueryProcessor

// NOTE: アプリケーションサービス や クエリプロセッサ にインフラを依存性注入して実行可能なインスタンスを生成します。

object Components {

  val TaskService = new TaskService with InfrastructureAware {
    override val taskRepository     = new TaskRepository with TaskRepositoryOnRDB
    override val taskEventPublisher = new TaskEventPublisher with TaskEventPublisherOnKafka
    override val userRepository     = new UserRepository with UserRepositoryOnRDB
  }

  val TaskViewQueryProcessor = new TaskViewQueryProcessor with TaskViewQueryProcessorOnES

}
