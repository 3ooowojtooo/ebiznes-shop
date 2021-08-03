package controllers.rest

import com.google.inject.Inject
import javax.inject.Singleton
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{AbstractController, ControllerComponents}
import repository.PurchaseHistoryRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PurchaseHistoryRestController @Inject()(cc: ControllerComponents, purchaseHistoryRepository: PurchaseHistoryRepository)(implicit val executionContext: ExecutionContext)
  extends AbstractController(cc) {

  implicit val createPurchaseHistoryFormatter: OFormat[CreatePurchaseHistory] = Json.format[CreatePurchaseHistory]
  implicit val updatePurchaseHistoryFormatter: OFormat[UpdatePurchaseHistory] = Json.format[UpdatePurchaseHistory]

  // GET /purchasehistory
  def getAll = Action.async { implicit request =>
    val purchaseHistory = purchaseHistoryRepository.list
    purchaseHistory.map(c => Ok(Json.toJson(c)))
  }

  // GET /purchasehistory/:id
  def findOne(id: Long) = Action.async { implicit request =>
    val purchaseHistory = purchaseHistoryRepository.getByIdOption(id)
    purchaseHistory.map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  // POST /purchasehistory
  def create = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[CreatePurchaseHistory](_).asOpt)
    requestBody match {
      case Some(newItem) =>
        purchaseHistoryRepository.create(newItem.cart, newItem.totalPrice, newItem.purchaseTimestamp)
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

  // DELETE /purchasehistory/:id
  def delete(id: Long) = Action.async {
    purchaseHistoryRepository.delete(id)
      .map(_ => Ok)
  }

  // PUT /purchasehistory/:id
  def update(id: Long) = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdatePurchaseHistory](_).asOpt)
    requestBody match {
      case Some(itemToUpdate) =>
        purchaseHistoryRepository.update(id, itemToUpdate.cart, itemToUpdate.totalPrice, itemToUpdate.purchaseTimestamp)
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

}

case class CreatePurchaseHistory(cart: Long, totalPrice: Double, purchaseTimestamp: String)

case class UpdatePurchaseHistory(cart: Long, totalPrice: Double, purchaseTimestamp: String)