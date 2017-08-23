package crossroad0201.dddonscala

import scala.annotation.StaticAnnotation

package object domain {

  /*
   * DDDにおけるどのコンポーネントなのかをわかりやすくするための
   * 説明用のアノテーションです。
   * 実際はこのようなアノテーションは作りません。
   */
  class entity extends StaticAnnotation
  class valueobject extends StaticAnnotation
  class factory extends StaticAnnotation
  class repository extends StaticAnnotation
  class domainservice extends StaticAnnotation // FIXME ドメインサービスの例がない
  class domainevent extends StaticAnnotation
  class eventpublisher extends StaticAnnotation

  // FIXME AnyVal を継承する？？
  // FIXME インフラ層でプリミティブ値とimplicit wrap/unwrapする
  trait Value[T] {
    val value: T
  }

  case class DomainResult[+ENTITY <: Entity[_ <: EntityId], +EVENT <: DomainEvent](entity: ENTITY, event: EVENT)

}
