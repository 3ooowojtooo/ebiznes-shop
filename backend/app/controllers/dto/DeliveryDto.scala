package controllers.dto

import models.{Cart, Delivery, User}
import play.api.libs.json.{Json, OFormat}

case class DeliveryDto(id: Long, cart: CartDto, deliveryTimestamp: String, isDelivered: Boolean)

object DeliveryDto {
  implicit val deliveryDtoFormat: OFormat[DeliveryDto] = Json.format[DeliveryDto]

  def apply(delivery : Delivery, cart : Cart, user : User) : DeliveryDto =
    DeliveryDto(delivery.id, CartDto(cart, user), delivery.deliveryTimestamp, delivery.isDelivered)
}