package controllers.dto

import models.{Cart, PaymentMethod, PurchaseHistory, UserAddress}
import play.api.libs.json.{Json, OFormat}

case class UserPurchaseHistoryDto (purchaseHistory : PurchaseHistory, cart : Cart, paymentMethod : PaymentMethod, address : UserAddress, cartItems : Seq[CartItemDto])

object UserPurchaseHistoryDto {
  implicit val userPurchaseHistoryDtoFormat: OFormat[UserPurchaseHistoryDto] = Json.format[UserPurchaseHistoryDto]
}
