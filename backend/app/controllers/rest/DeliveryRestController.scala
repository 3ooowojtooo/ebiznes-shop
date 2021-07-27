package controllers.rest

import com.google.inject.Inject
import javax.inject.Singleton
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{AbstractController, ControllerComponents}
import repository.{DeliveryRepository, PurchaseHistoryRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeliveryRestController @Inject()(cc : ControllerComponents, deliveryRepository : DeliveryRepository)(implicit val executionContext : ExecutionContext)
extends AbstractController(cc) {

  implicit val createDeliveryFormatter : OFormat[CreateDelivery] = Json.format[CreateDelivery]
  implicit val updateDeliveryFormatter : OFormat[UpdateDelivery] = Json.format[UpdateDelivery]

  // GET /delivery
  def getAll = Action.async {implicit request =>
    val delivery = deliveryRepository.list
    delivery.map(c => Ok(Json.toJson(c)))
  }

  // GET /delivery/:id
  def findOne(id : Long) = Action.async {implicit request =>
    val delivery = deliveryRepository.getByIdOption(id)
    delivery.map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  // POST /delivery
  def create = Action.async {implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[CreateDelivery](_).asOpt)
    requestBody match {
      case Some(newItem) =>
        deliveryRepository.create(newItem.cart, newItem.deliveryTimestamp, newItem.isDelivered)
        .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

  // DELETE /delivery/:id
  def delete(id : Long) = Action.async {
    deliveryRepository.delete(id)
      .map(_ => Ok)
  }

  // PUT /delivery/:id
  def update(id : Long) = Action.async {implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdateDelivery](_).asOpt)
    requestBody match {
      case Some(itemToUpdate) =>
        deliveryRepository.update(id, itemToUpdate.cart, itemToUpdate.deliveryTimestamp, itemToUpdate.isDelivered)
        .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

}

case class CreateDelivery(cart : Long, deliveryTimestamp : String, isDelivered : Boolean)
case class UpdateDelivery(cart : Long, deliveryTimestamp : String, isDelivered : Boolean)