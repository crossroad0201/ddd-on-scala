package crossroad0201.dddonscala.domain.task

import crossroad0201.dddonscala.domain.UnitOfWork

import scala.util.Try

/*
 * NOTE: リポジトリはドメインレイヤからは呼び出さず、アプリケーションサービスからのみ呼び出すようにします。
 * リポジトリの責務はエンティティの永続化ですが、永続化は副作用となるため、
 * エンティティなどから呼び出すとエンティティが副作用を起こすことになってしまいます。
 * リポジトリの呼び出しをアプリケーションサービスに限定することで、副作用を局所化できます。
 */

trait TaskRepository {

  def get(id: TaskId)(implicit uof: UnitOfWork): Try[Option[Task]]

  def save(task: Task)(implicit uof: UnitOfWork): Try[Task]

  def delete(task: Task)(implicit uof: UnitOfWork): Try[Task]

}
