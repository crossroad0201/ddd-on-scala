package crossroad0201.dddonscala

import java.util.UUID

import crossroad0201.dddonscala.application.{ConflictedError, ServiceError, SystemError}
import crossroad0201.dddonscala.domain.{EntityIdGenerator, EntityMetaData, Value}
import crossroad0201.dddonscala.infrastructure.rdb.OptimisticLockException

import scala.language.implicitConversions

package object infrastructure {

  /*
   * プリミティブな値 を ドメインの値型 に自動的に変換する implicit関数 です。
   * 型クラスのパターンで実装しており、個々の値型に対応する変換関数を implicit引数 として取ります。
   */
  implicit def wrapValue[P, V <: Value[P]](value: P)(implicit conv: P => V): V = conv(value)
  /*
   * ドメインの値型 を プリミティブな値 に自動変換する implicit関数 です。
   */
  implicit def unwrapValue[P](value: Value[P]): P = value.value

  implicit def wrapEntityMetaData(version: Int): EntityMetaData = EntityMetaDataImpl(version)

}
