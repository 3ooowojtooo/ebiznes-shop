package controllers.dto

import models.{Cart, PurchaseHistory, User}
import play.api.libs.json.{Json, OFormat}

case class PurchaseHistoryDto(id: Long, cart: CartDto, totalPrice: Double, purchaseTimestamp: String)

object PurchaseHistoryDto {
  implicit val purchaseHistoryDtoFormat: OFormat[PurchaseHistoryDto] = Json.format[PurchaseHistoryDto]

  def apply(purchaseHistory : PurchaseHistory, cart : Cart, user : User) : PurchaseHistoryDto =
    PurchaseHistoryDto(purchaseHistory.id, CartDto(cart, user), purchaseHistory.totalPrice, purchaseHistory.purchaseTimestamp)
}
