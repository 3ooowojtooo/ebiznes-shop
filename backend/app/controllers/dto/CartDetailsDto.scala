package controllers.dto

import models.Cart
import play.api.libs.json.{Json, OFormat}

case class CartDetailsDto(id: Long, createdTime: String, items: Seq[CartItemDetailsDto])

object CartDetailsDto {
  implicit val cartDetailsDtoFormatter : OFormat[CartDetailsDto] = Json.format[CartDetailsDto]

  def apply(cart : Cart, items : Seq[CartItemDto]) : CartDetailsDto = {
    val mappedItems = items.map(i => CartItemDetailsDto(i, cart))
    CartDetailsDto(cart.id, cart.createdTime, mappedItems)
  }
}
