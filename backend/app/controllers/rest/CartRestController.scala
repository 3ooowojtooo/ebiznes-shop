package controllers.rest

import controllers.auth.{AbstractAuthController, DefaultSilhouetteControllerComponents}
import controllers.dto.CartDetailsDto
import play.api.libs.json.{Json, OFormat}
import repository.{CartItemRepository, CartRepository}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CartRestController @Inject()(cc: DefaultSilhouetteControllerComponents, cartRepository: CartRepository,
                                   cartItemRepository: CartItemRepository)(implicit val executionContext: ExecutionContext)
  extends AbstractAuthController(cc) {

  // GET /cart
  def getAll = Action.async { implicit request =>
    val carts = cartRepository.list
    carts.map(p => Ok(Json.toJson(p)))
  }

  // GET /cart/:id
  def findOne(id: Long) = Action.async { implicit request =>
    val cart = cartRepository.getByIdOption(id)
    cart.map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  implicit val createCartFormatter: OFormat[CreateCart] = Json.format[CreateCart]

  // POST /cart
  def create = Action.async { implicit request =>
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
  def delete(id: Long) = Action.async {
    cartRepository.delete(id)
      .map(_ => Ok)
  }

  implicit val updateCartFormatter: OFormat[UpdateCart] = Json.format[UpdateCart]

  // PUT /cart/id
  def update(id: Long) = Action.async { implicit request =>
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
  def getOrCreateUserCart = silhouette.SecuredAction.async { implicit request =>
    cartRepository.getOrCreateUserCart(request.identity.id)
      .flatMap(cart =>
        cartItemRepository.listByCartId(cart.id)
          .map(items => Ok(Json.toJson(CartDetailsDto(cart, items))))
      )
  }

  // POST /currentcart/product/:productId
  def addToUserCart(productId: Long) = silhouette.SecuredAction.async { implicit request =>
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
  def deleteFromUserCart(itemId: Long) = silhouette.SecuredAction.async { implicit request =>
    cartRepository.getUserCartId(request.identity.id)
      .flatMap {
        case None => Future.successful(Ok)
        case Some(cartId) =>
          cartItemRepository.deleteByIdAndCartId(cartId, itemId)
            .map(_ => Ok
        )
      }
  }
}

case class CreateCart(createdTime: String, user: Long, purchased: Boolean)

case class UpdateCart(createdTime: String, user: Long, purchased: Boolean)