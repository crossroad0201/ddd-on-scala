package crossroad0201.dddonscala.infrastructure

import crossroad0201.dddonscala.domain.user.{UserId, UserName}

package object user {

  implicit val toUserId:   String => UserId   = (v) => UserId(v)
  implicit val toUserName: String => UserName = (v) => UserName(v)

}
