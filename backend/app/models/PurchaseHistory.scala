package models

import play.api.libs.json.{Json, OFormat}

case class PurchaseHistory(id: Long, cart: Long, paymentMethod : Long, userAddress : Long, totalPrice: Double, purchaseTimestamp: String)

object PurchaseHistory {
  implicit val purchaseHistoryFormat: OFormat[PurchaseHistory] = Json.format[PurchaseHistory]
}
