package controllers.rest

import controllers.auth.{AbstractAuthController, DefaultSilhouetteControllerComponents}
import controllers.dto.{CartDetailsDto, CartItemDto}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{Action, AnyContent, Result}
import repository.{CartItemRepository, CartRepository, DeliveryRepository, PurchaseHistoryRepository, StockRepository}

import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.{Inject, Singleton}
import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CartRestController @Inject()(cc: DefaultSilhouetteControllerComponents, cartRepository: CartRepository,
                                   cartItemRepository: CartItemRepository, purchaseHistoryRepository: PurchaseHistoryRepository,
                                   deliveryRepository: DeliveryRepository, stockRepository: StockRepository)(implicit val executionContext: ExecutionContext)
  extends AbstractAuthController(cc) {

  // GET /cart
  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val carts = cartRepository.list
    carts.map(p => Ok(Json.toJson(p)))
  }

  // GET /cart/:id
  def findOne(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val cart = cartRepository.getByIdOption(id)
    cart.map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  implicit val createCartFormatter: OFormat[CreateCart] = Json.format[CreateCart]

  // POST /cart
  def create: Action[AnyContent] = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[CreateCart](_).asOpt)
    requestBody match {
      case Some(newItem) =>
        cartRepository.create(newItem.createdTime, newItem.user, newItem.purchased)
          .map(p => Created(Json.toJson(p)))
      case None =>
        Future(BadRequest)
    }
  }

  // DELETE /cart/id
  def delete(id: Long): Action[AnyContent] = Action.async {
    cartRepository.delete(id)
      .map(_ => Ok)
  }

  implicit val updateCartFormatter: OFormat[UpdateCart] = Json.format[UpdateCart]

  // PUT /cart/id
  def update(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdateCart](_).asOpt)
    requestBody match {
      case Some(itemToUpdate) =>
        cartRepository.update(id, itemToUpdate.createdTime, itemToUpdate.user, itemToUpdate.purchased)
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

  // GET /currentcart
  def getOrCreateUserCart: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    cartRepository.getOrCreateUserCart(request.identity.id)
      .flatMap(cart =>
        cartItemRepository.listByCartId(cart.id)
          .map(items => Ok(Json.toJson(CartDetailsDto(cart, items))))
      )
  }

  // POST /currentcart/product/:productId
  def addToUserCart(productId: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    cartRepository.getUserCartId(request.identity.id)
      .flatMap {
        case None => Future.successful(Ok)
        case Some(cartId) =>
          cartItemRepository.getByCartAndProduct(cartId, productId)
            .flatMap {
              case Some(cartItem) => cartItemRepository.update(cartItem.id, cartItem.product, cartItem.amount + 1, cartItem.cart)
              case None => cartItemRepository.create(productId, 1, cartId).map(_ => ())
            }
            .map(_ => Ok)
      }
  }

  // DELETE /currentcart/item/:itemId
  def deleteFromUserCart(itemId: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    cartRepository.getUserCartId(request.identity.id)
      .flatMap {
        case None => Future.successful(Ok)
        case Some(cartId) =>
          cartItemRepository.deleteByIdAndCartId(cartId, itemId)
            .map(_ => Ok)
      }
  }

  implicit val buyCartFormatter: OFormat[BuyCart] = Json.format[BuyCart]

  // POST /currentcart
  def buyUserCart: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[BuyCart](_).asOpt)
    requestBody match {
      case Some(req) =>
        cartRepository.getUserCartId(request.identity.id)
          .flatMap {
            case Some(cartId) => buyCart(cartId, req.paymentMethod, req.userAddress)
            case None => Future(Ok)
          }
      case None => Future(BadRequest)
    }
  }

  val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  private def buyCart(cartId: Long, paymentMethodId: Long, userAddressId: Long): Future[Status] = {
    cartRepository.getById(cartId)
      .flatMap(cart => {
        cartRepository.update(cart.id, cart.createdTime, cart.user.id, purchased = true)
          .flatMap(_ => {
            cartItemRepository.listByCartId(cartId)
              .flatMap(items => {
                purchaseHistoryRepository.create(cartId, computeCartItemsTotalPrice(items), dateFormat.format(new Date()))
                  .flatMap(_ => {
                    deliveryRepository.create(cartId, dateFormat.format(new Date()), delivered = true)
                      .flatMap(_ => {
                        val futures = ListBuffer[Future[Unit]]()
                        items.foreach(item => futures += stockRepository.decreaseStockAmount(item.product.id, item.amount))
                        Future.sequence(futures.toList)
                      })
                  })
                  .map(_ => Ok)
              })
          })
      })
  }

  private def computeCartItemsTotalPrice(items: Seq[CartItemDto]): Double = {
    var totalPrice : Double = 0
    items.foreach(item => totalPrice += item.amount * item.product.price)
    totalPrice
  }
}

case class CreateCart(createdTime: String, user: Long, purchased: Boolean)

case class UpdateCart(createdTime: String, user: Long, purchased: Boolean)

case class BuyCart(paymentMethod: Long, userAddress: Long)