package controllers.rest

import com.google.inject.Inject
import controllers.auth.{AbstractAuthController, DefaultSilhouetteControllerComponents}
import controllers.dto.{CartItemDto, UserPurchaseHistoryDto}
import models.{Cart, PaymentMethod, PurchaseHistory, UserAddress}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{Action, AnyContent}
import repository.{CartItemRepository, PurchaseHistoryRepository}

import javax.inject.Singleton
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PurchaseHistoryRestController @Inject()(cc: DefaultSilhouetteControllerComponents, purchaseHistoryRepository: PurchaseHistoryRepository,
                                              cartItemRepository: CartItemRepository)(implicit val executionContext: ExecutionContext)
  extends AbstractAuthController(cc) {

  implicit val createPurchaseHistoryFormatter: OFormat[CreatePurchaseHistory] = Json.format[CreatePurchaseHistory]
  implicit val updatePurchaseHistoryFormatter: OFormat[UpdatePurchaseHistory] = Json.format[UpdatePurchaseHistory]

  // GET /purchasehistory
  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val purchaseHistory = purchaseHistoryRepository.list
    purchaseHistory.map(c => Ok(Json.toJson(c)))
  }

  // GET /purchasehistory/:id
  def findOne(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val purchaseHistory = purchaseHistoryRepository.getByIdOption(id)
    purchaseHistory.map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  // POST /purchasehistory
  def create: Action[AnyContent] = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[CreatePurchaseHistory](_).asOpt)
    requestBody match {
      case Some(newItem) =>
        purchaseHistoryRepository.create(newItem.cart, newItem.paymentMethod, newItem.address, newItem.totalPrice, newItem.purchaseTimestamp)
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

  // DELETE /purchasehistory/:id
  def delete(id: Long): Action[AnyContent] = Action.async {
    purchaseHistoryRepository.delete(id)
      .map(_ => Ok)
  }

  // PUT /purchasehistory/:id
  def update(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdatePurchaseHistory](_).asOpt)
    requestBody match {
      case Some(itemToUpdate) =>
        purchaseHistoryRepository.update(id, itemToUpdate.cart, itemToUpdate.paymentMethod, itemToUpdate.address, itemToUpdate.totalPrice, itemToUpdate.purchaseTimestamp)
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

  // GET /currentpurchasehistory
  def getUserPurchaseHistory: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    purchaseHistoryRepository.getUserHistory(request.identity.id)
      .flatMap(entries => {
        val futures = ListBuffer[Future[(PurchaseHistory, Cart, PaymentMethod, UserAddress, Seq[CartItemDto])]]()
        entries.foreach(entry => {
          futures += cartItemRepository.listByCartId(entry._2.id)
            .map(cartItems => (entry._1, entry._2, entry._3, entry._4, cartItems))
        })
        Future.sequence(futures.toList)
      }.map(_.toStream
        .map(item => UserPurchaseHistoryDto(item._1, item._2, item._3, item._4, item._5))
        .toList)
        .map(items => Ok(Json.toJson(items)))
      )
  }
}

case class CreatePurchaseHistory(cart: Long, paymentMethod: Long, address: Long, totalPrice: Double, purchaseTimestamp: String)

case class UpdatePurchaseHistory(cart: Long, paymentMethod: Long, address: Long, totalPrice: Double, purchaseTimestamp: String)