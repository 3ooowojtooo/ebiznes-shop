package controllers.rest

import javax.inject.{Inject, Singleton}
import models.Product
import play.api.libs.json.{Json, OFormat, __}
import play.api.mvc._
import repository.{CategoryRepository, StockRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockRestController @Inject()(cc: ControllerComponents, stockRepository: StockRepository)(implicit val executionContext : ExecutionContext)
  extends AbstractController(cc) {

  // GET /stock
  def getAll = Action.async {implicit request =>
    val stocks = stockRepository.list
    stocks.map(p => Ok(Json.toJson(p)))
  }

  // GET /stock/:id
  def findOne(id : Long) = Action.async {implicit request =>
    val stock = stockRepository.getByIdOption(id)
    stock.map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  implicit val createStockFormatter: OFormat[CreateStock] = Json.format[CreateStock]

  // POST /stock
  def create = Action.async {implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[CreateStock](_).asOpt)
    requestBody match {
      case Some(newItem) =>
        stockRepository.create(newItem.product, newItem.amount)
        .map(p => Created(Json.toJson(p)))
      case None =>
        Future(BadRequest)
    }
  }

  // DELETE /stock/id
  def delete(id : Long) = Action.async {
    stockRepository.delete(id)
      .map(_ => Ok)
  }

  implicit val updateStockFormatter: OFormat[UpdateStock] = Json.format[UpdateStock]

  // PUT /stock/id
  def update(id : Long) = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdateStock](_).asOpt)
    requestBody match {
      case Some(itemToUpdate) =>
        stockRepository.update(id, itemToUpdate.product, itemToUpdate.amount)
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }
}

case class CreateStock(product : Long, amount : Long)
case class UpdateStock(product : Long, amount : Long)
