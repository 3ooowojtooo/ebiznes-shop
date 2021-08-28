package controllers.dto

import models.{Cart, CartItem, Category, Product, User}
import play.api.libs.json.{Json, OFormat}

case class CartItemDto(id: Long, cart: CartDto, product: ProductDto, amount: Long)

object CartItemDto {
  implicit val cartItemDtoFormat: OFormat[CartItemDto] = Json.format[CartItemDto]

  def apply(cartItem : CartItem, product : Product, cart : Cart, user : UserDto, category : Category) : CartItemDto =
    CartItemDto(cartItem.id, CartDto(cart, user), ProductDto(product, category), cartItem.amount)
}
