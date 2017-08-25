package crossroad0201.dddonscala

import scala.annotation.Annotation

package object domain {

  /*
   * DDDにおけるどのコンポーネントなのかをわかりやすくするための
   * 説明用のアノテーションです。
   * 実際はこのようなアノテーションは作りません。
   */
  class entity extends Annotation
  class valueobject extends Annotation
  class factory extends Annotation
  class repository extends Annotation
  class domainservice extends Annotation // FIXME ドメインサービスの例がない
  class domainevent extends Annotation
  class eventpublisher extends Annotation

  // FIXME AnyVal を継承する？？
  // FIXME インフラ層でプリミティブ値とimplicit wrap/unwrapする
  trait Value[T] {
    val value: T
  }

  case class DomainResult[+ENTITY <: Entity[_ <: EntityId], +EVENT <: DomainEvent](entity: ENTITY, event: EVENT)

}
