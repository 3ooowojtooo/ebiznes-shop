package controllers.rest

import javax.inject.{Inject, Singleton}
import models.Product
import play.api.libs.json.{Json, OFormat, __}
import play.api.mvc._
import repository.{CategoryRepository, StockRepository, UserAddressRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAddressRestController @Inject()(cc: ControllerComponents, userAddressRepository: UserAddressRepository)(implicit val executionContext : ExecutionContext)
  extends AbstractController(cc) {

  // GET /address
  def getAll = Action.async {implicit request =>
    val addresses = userAddressRepository.list
    addresses.map(p => Ok(Json.toJson(p)))
  }

  // GET /address/:id
  def findOne(id : Long) = Action.async {implicit request =>
    val address = userAddressRepository.getByIdOption(id)
    address.map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  implicit val createUserAddressFormatter: OFormat[CreateUserAddress] = Json.format[CreateUserAddress]

  // POST /address
  def create = Action.async {implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[CreateUserAddress](_).asOpt)
    requestBody match {
      case Some(newItem) =>
        userAddressRepository.create(newItem.street, newItem.city, newItem.zipcode, newItem.user)
        .map(p => Created(Json.toJson(p)))
      case None =>
        Future(BadRequest)
    }
  }

  // DELETE /address/id
  def delete(id : Long) = Action.async {
    userAddressRepository.delete(id)
      .map(_ => Ok)
  }

  implicit val updateUserAddressFormatter: OFormat[UpdateUserAddress] = Json.format[UpdateUserAddress]

  // PUT /address/id
  def update(id : Long) = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdateUserAddress](_).asOpt)
    requestBody match {
      case Some(itemToUpdate) =>
        userAddressRepository.update(id, itemToUpdate.street, itemToUpdate.city, itemToUpdate.zipcode, itemToUpdate.user)
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }
}

case class CreateUserAddress(street : String, city : String, zipcode : String, user : Long)
case class UpdateUserAddress(street : String, city : String, zipcode : String, user : Long)
