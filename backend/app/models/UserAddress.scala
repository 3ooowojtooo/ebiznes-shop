package models

import play.api.libs.json.{Json, OFormat}

case class UserAddress(id: Long, street: String, city: String, zipcode: String, user: Long)

object UserAddress {
  implicit val userAddressFormat: OFormat[UserAddress] = Json.format[UserAddress]
}


