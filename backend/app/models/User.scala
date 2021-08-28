package models

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import controllers.dto.UserDto
import play.api.libs.json.{Json, OFormat}

case class User(id: Long, email : String, loginInfo : LoginInfo) extends Identity

object User {
  implicit val userFormat: OFormat[User] = Json.format[User]
  implicit val loginInfoFormat : OFormat[LoginInfo] = Json.format[LoginInfo]

  def apply(userDto : UserDto) : User = User(userDto.id, userDto.email, LoginInfo(userDto.providerId, userDto.providerKey))
}
