package controllers.dto

import models.User
import play.api.libs.json.{Json, OFormat}

case class UserDto(id: Long, name: String, age: Long)

object UserDto {
  implicit val userDtoFormat: OFormat[UserDto] = Json.format[UserDto]

  def apply(user : User): UserDto = UserDto(user.id, user.name, user.age)
}

