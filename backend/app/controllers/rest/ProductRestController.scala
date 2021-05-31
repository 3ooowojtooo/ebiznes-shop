package controllers.rest

import javax.inject.{Inject, Singleton}
import models.Product
import play.api.libs.json.{Json, OFormat, __}
import play.api.mvc._
import repository.ProductRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductRestController @Inject()(cc: ControllerComponents, productRepository: ProductRepository)(implicit val executionContext : ExecutionContext)
  extends AbstractController(cc) {

  // GET /product
  def getAll = Action.async {implicit request =>
    val products = productRepository.list
    products.map(p => Ok(Json.toJson(p)))
  }

  // GET /product/:id
  def findOne(id : Long) = Action.async {implicit request =>
    val product = productRepository.getByIdOption(id)
    product.map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  implicit val createProductFormatter: OFormat[CreateProduct] = Json.format[CreateProduct]

  // POST /product
  def create = Action.async {implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[CreateProduct](_).asOpt)
    requestBody match {
      case Some(newItem) =>
        productRepository.create(newItem.name, newItem.description, newItem.category, newItem.price)
        .map(p => Created(Json.toJson(p)))
      case None =>
        Future(BadRequest)
    }
  }

  // DELETE /product/id
  def delete(id : Long) = Action.async {
    productRepository.delete(id)
      .map(_ => Ok)
  }

  implicit val updateProductFormatter: OFormat[UpdateProduct] = Json.format[UpdateProduct]

  // PUT /product/id
  def update(id : Long) = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdateProduct](_).asOpt)
    requestBody match {
      case Some(itemToUpdate) =>
        productRepository.update(id, itemToUpdate.name, itemToUpdate.description, itemToUpdate.category, itemToUpdate.price)
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }
}

case class CreateProduct(name : String, description : String, category : Long, price : Double)
case class UpdateProduct(name : String, description : String, category : Long, price : Double)
