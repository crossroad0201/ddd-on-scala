package crossroad0201.dddonscala.infrastructure

import crossroad0201.dddonscala.domain.user.UserId

package object user {

  implicit val toUserId: String => UserId = (v) => UserId(v)

}
