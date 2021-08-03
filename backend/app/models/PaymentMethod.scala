package models

import play.api.libs.json.{Json, OFormat}

case class PaymentMethod(id: Long, user: Long, name: String)

object PaymentMethod {
  implicit val paymentMethodFormat: OFormat[PaymentMethod] = Json.format[PaymentMethod]
}
