package crossroad0201.dddonscala.application

import crossroad0201.dddonscala.domain.UnitOfWork

/*
 * NOTE: アプリケーションサービスをトランザクション境界とするためのトレイトです。
 * 実際にどうトランザクション制御するかは、採用するミドルウェア・サービスに依存するため、インフラレイヤで実装します。
 */

trait TransactionAware {

  def tx[A](f: UnitOfWork => A): A

  def txReadonly[A](f: UnitOfWork => A): A

}
