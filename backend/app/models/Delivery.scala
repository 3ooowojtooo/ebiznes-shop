package models

import play.api.libs.json.{Json, OFormat}

case class Delivery(id: Long, cart: Long, deliveryTimestamp: String, isDelivered: Boolean)

object Delivery {
  implicit val deliveryFormat: OFormat[Delivery] = Json.format[Delivery]
}
