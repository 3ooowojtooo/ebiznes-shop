package controllers.rest

import com.google.inject.Inject
import javax.inject.Singleton
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{AbstractController, ControllerComponents}
import repository.CartItemRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CartItemRestController @Inject()(cc: ControllerComponents, cartItemRepository: CartItemRepository)(implicit val executionContext: ExecutionContext)
  extends AbstractController(cc) {

  implicit val createCartItemFormatter: OFormat[CreateCartItem] = Json.format[CreateCartItem]
  implicit val updateCartItemFormatter: OFormat[UpdateCartItem] = Json.format[UpdateCartItem]

  // GET /cartitem
  def getAll = Action.async { implicit request =>
    val cartItems = cartItemRepository.list
    cartItems.map(c => Ok(Json.toJson(c)))
  }

  // GET /cartitem/:id
  def findOne(id: Long) = Action.async { implicit request =>
    val cartItem = cartItemRepository.getByIdOption(id)
    cartItem.map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  // POST /cartitem
  def create = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[CreateCartItem](_).asOpt)
    requestBody match {
      case Some(newItem) =>
        cartItemRepository.create(newItem.product, newItem.amount, newItem.cart)
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

  // DELETE /cartitem/:id
  def delete(id: Long) = Action.async {
    cartItemRepository.delete(id)
      .map(_ => Ok)
  }

  // PUT /cartitem/:id
  def update(id: Long) = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdateCartItem](_).asOpt)
    requestBody match {
      case Some(itemToUpdate) =>
        cartItemRepository.update(id, itemToUpdate.product, itemToUpdate.amount, itemToUpdate.cart)
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

}

case class CreateCartItem(cart: Long, product: Long, amount: Long)

case class UpdateCartItem(cart: Long, product: Long, amount: Long)