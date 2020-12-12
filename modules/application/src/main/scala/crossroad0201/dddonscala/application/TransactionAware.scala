package crossroad0201.dddonscala.application

import crossroad0201.dddonscala.domain.UnitOfWork

/*
 * NOTE: アプリケーションサービスをトランザクション境界とするためのトレイトです。
 * 実際にどうトランザクション制御するかは、採用するミドルウェア・サービスに依存するため、インフラレイヤで実装します。
 *
 * NOTE: 2020-12-12追記 このサンプルコードではアプリケーションサービスをトランザクション境界としていますが、これは推奨しません。
 * 原則としてトランザクション境界は集約単位とし、集約間は結果整合性として設計したほうが良いです。
 */

trait TransactionAware {

  def tx[A](f: UnitOfWork => A): A

  def txReadonly[A](f: UnitOfWork => A): A

}
