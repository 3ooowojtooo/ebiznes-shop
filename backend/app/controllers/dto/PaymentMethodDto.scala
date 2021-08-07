package controllers.dto

import models.{PaymentMethod, User}
import play.api.libs.json.{Json, OFormat}

case class PaymentMethodDto(id: Long, user: UserDto, name: String)

object PaymentMethodDto {
  implicit val paymentMethodDtoFormat: OFormat[PaymentMethodDto] = Json.format[PaymentMethodDto]

  def apply(paymentMethod : PaymentMethod, user : User) : PaymentMethodDto =
    PaymentMethodDto(paymentMethod.id, UserDto(user), paymentMethod.name)
}
