package controllers.dto

import models.Cart
import play.api.libs.json.{Json, OFormat}

case class CartItemDetailsDto(id: Long, cartId: Long, product: ProductDto, amount: Long)

object CartItemDetailsDto {
  implicit val cartItemDetailsDtoFormat: OFormat[CartItemDetailsDto] = Json.format[CartItemDetailsDto]

  def apply(cartItem: CartItemDto, cart: Cart) : CartItemDetailsDto =
    CartItemDetailsDto(cartItem.id, cart.id, cartItem.product, cartItem.amount)
}
