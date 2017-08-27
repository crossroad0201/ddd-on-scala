package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.UnitOfWork

import scala.util.Try

/*
 * NOTE: イベントパブリッシャーはドメインレイヤからは呼び出さず、アプリケーションサービスからのみ呼び出すようにします。
 * イベントパブリッシャーの責務はドメインイベントの永続化ですが、永続化は副作用となるため、
 * エンティティなどから呼び出すとエンティティが副作用を起こすことになってしまいます。
 * イベントパブリッシャーの呼び出しをアプリケーションサービスに限定することで、副作用を局所化できます。
 */

trait TaskEventPublisher {

  def publish[EVENT <: TaskEvent](event: EVENT)(implicit uow: UnitOfWork): Try[EVENT]

}
