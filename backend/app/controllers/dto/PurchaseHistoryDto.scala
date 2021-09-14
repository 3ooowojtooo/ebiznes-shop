package controllers.dto

import models.{Cart, PaymentMethod, PurchaseHistory, UserAddress}
import play.api.libs.json.{Json, OFormat}

case class PurchaseHistoryDto(id: Long, cart: CartDto, paymentMethod: PaymentMethodDto, address: UserAddressDto,
                              totalPrice: Double, purchaseTimestamp: String)

object PurchaseHistoryDto {
  implicit val purchaseHistoryDtoFormat: OFormat[PurchaseHistoryDto] = Json.format[PurchaseHistoryDto]

  def apply(purchaseHistory: PurchaseHistory, cart: Cart, user: UserDto, paymentMethod: PaymentMethod,
            userAddress: UserAddress): PurchaseHistoryDto =
    PurchaseHistoryDto(purchaseHistory.id, CartDto(cart, user), PaymentMethodDto(paymentMethod, user),
      UserAddressDto(userAddress, user), purchaseHistory.totalPrice, purchaseHistory.purchaseTimestamp)
}
