package controllers.dto

import models.{Cart, User}
import play.api.libs.json.{Json, OFormat}

case class CartDto(id: Long, createdTime: String, user: UserDto, purchased: Boolean)

object CartDto {
  implicit val cartDtoFormat: OFormat[CartDto] = Json.format[CartDto]

  def apply(cart : Cart, user : UserDto) : CartDto =
    CartDto(cart.id, cart.createdTime, user, cart.purchased)
}