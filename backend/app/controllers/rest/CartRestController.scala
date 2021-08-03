package controllers.rest

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._
import repository.CartRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CartRestController @Inject()(cc: ControllerComponents, cartRepository: CartRepository)(implicit val executionContext: ExecutionContext)
  extends AbstractController(cc) {

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
}

case class CreateCart(createdTime: String, user: Long, purchased: Boolean)

case class UpdateCart(createdTime: String, user: Long, purchased: Boolean)
