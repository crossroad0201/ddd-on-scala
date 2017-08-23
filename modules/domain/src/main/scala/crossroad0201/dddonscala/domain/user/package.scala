package crossroad0201.dddonscala.domain

package object user {

  @valueobject
  case class UserId(value: String) extends EntityId

}
