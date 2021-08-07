package controllers.dto

import models.{Category, Product, Stock}
import play.api.libs.json.{Json, OFormat}

case class StockDto(id: Long, product: ProductDto, amount: Long)

object StockDto {
  implicit val stockDtoFormat: OFormat[StockDto] = Json.format[StockDto]

  def apply(stock : Stock, product : Product, category : Category) : StockDto =
    StockDto(stock.id, ProductDto(product, category), stock.amount)
}
