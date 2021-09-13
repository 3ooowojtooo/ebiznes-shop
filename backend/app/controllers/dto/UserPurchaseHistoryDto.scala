package controllers.dto

import models.{Cart, PurchaseHistory}
import play.api.libs.json.{Json, OFormat}

case class UserPurchaseHistoryDto (purchaseHistory : PurchaseHistory, cart : Cart, cartItems : Seq[CartItemDto])

object UserPurchaseHistoryDto {
  implicit val userPurchaseHistoryDtoFormat: OFormat[UserPurchaseHistoryDto] = Json.format[UserPurchaseHistoryDto]
}
