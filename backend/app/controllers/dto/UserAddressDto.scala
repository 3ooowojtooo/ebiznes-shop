package controllers.dto

import models.{User, UserAddress}
import play.api.libs.json.{Json, OFormat}

case class UserAddressDto(id: Long, street: String, city: String, zipcode: String, user: UserDto)

object UserAddressDto {
  implicit val userAddressDtoFormat: OFormat[UserAddressDto] = Json.format[UserAddressDto]

  def apply(userAddress : UserAddress, user : User) : UserAddressDto =
    UserAddressDto(userAddress.id, userAddress.street, userAddress.city, userAddress.zipcode, UserDto(user))
}
