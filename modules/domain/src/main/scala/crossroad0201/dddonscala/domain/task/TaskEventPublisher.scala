package crossroad0201.dddonscala.domain.task

import scala.util.Try

// FIXME イベントパブリッシャは、集約ごとに作らずに共通（型クラスを使う）でもいいかもしれない
trait TaskEventPublisher {

  def publish(event: TaskCreated): Try[TaskCreated]
  def publish(event: TaskAssigned): Try[TaskAssigned]

}
