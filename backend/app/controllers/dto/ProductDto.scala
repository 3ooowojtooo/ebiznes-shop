package controllers.dto

import models.{Category, Product}
import play.api.libs.json.{Json, OFormat}

case class ProductDto(id: Long, name: String, description: String, category: CategoryDto, price: Double)

object ProductDto {
  implicit val productDtoFormat: OFormat[ProductDto] = Json.format[ProductDto]

  def apply(product : Product, category : Category): ProductDto =
    ProductDto(product.id, product.name, product.description, CategoryDto(category), product.price)
}
