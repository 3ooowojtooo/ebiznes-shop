package controllers.rest

import controllers.auth.{AbstractAuthController, DefaultSilhouetteControllerComponents}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._
import repository.UserAddressRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAddressRestController @Inject()(cc: DefaultSilhouetteControllerComponents, userAddressRepository: UserAddressRepository)(implicit val executionContext: ExecutionContext)
  extends AbstractAuthController(cc) {

  // GET /address
  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val addresses = userAddressRepository.list
    addresses.map(p => Ok(Json.toJson(p)))
  }

  // GET /address/:id
  def findOne(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val address = userAddressRepository.getByIdOption(id)
    address.map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  implicit val createUserAddressFormatter: OFormat[CreateUserAddress] = Json.format[CreateUserAddress]

  // POST /address
  def create: Action[AnyContent] = Action.async { implicit request =>
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
  def delete(id: Long): Action[AnyContent] = Action.async {
    userAddressRepository.delete(id)
      .map(_ => Ok)
  }

  implicit val updateUserAddressFormatter: OFormat[UpdateUserAddress] = Json.format[UpdateUserAddress]

  // PUT /address/id
  def update(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdateUserAddress](_).asOpt)
    requestBody match {
      case Some(itemToUpdate) =>
        userAddressRepository.update(id, itemToUpdate.street, itemToUpdate.city, itemToUpdate.zipcode, itemToUpdate.user)
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

  // GET /currentaddress
  def getUserAddresses: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    userAddressRepository.getUserAddresses(request.identity.id)
      .map(addresses => Ok(Json.toJson(addresses)))
  }

  implicit val updateCurrentUserAddressFormatter: OFormat[UpdateCurrentUserAddress] = Json.format[UpdateCurrentUserAddress]

  // PUT /currentaddress/:id
  def updateCurrentUserAddress(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdateCurrentUserAddress](_).asOpt)
    requestBody match {
      case Some(itemToUpdate) =>
        userAddressRepository.getByUserStreetCityAndZipcode(request.identity.id, itemToUpdate.street, itemToUpdate.city, itemToUpdate.zipcode)
          .flatMap {
            case Some(_) => Future.successful(BadRequest("Specified user address already exists"))
            case None => userAddressRepository.update(id, itemToUpdate.street, itemToUpdate.city, itemToUpdate.zipcode, request.identity.id)
              .map(_ => Ok)
          }
      case None => Future(BadRequest)
    }
  }

  // POST /currentaddress
  def createUserAddress: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdateCurrentUserAddress](_).asOpt)
    requestBody match {
      case Some(itemToCreate) =>
        userAddressRepository.getByUserStreetCityAndZipcode(request.identity.id, itemToCreate.street, itemToCreate.city, itemToCreate.zipcode)
          .flatMap {
            case Some(_) => Future.successful(BadRequest("Specified user address already exists"))
            case None =>
              userAddressRepository.create(itemToCreate.street, itemToCreate.city, itemToCreate.zipcode, request.identity.id)
                .map(_ => Ok)
          }
      case None => Future(BadRequest)
    }
  }

  // DELETE /currentaddress/:id
  def deleteUserPaymentMethod(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    userAddressRepository.delete(id, request.identity.id)
      .map(_ => Ok)
  }
}

case class CreateUserAddress(street: String, city: String, zipcode: String, user: Long)

case class UpdateUserAddress(street: String, city: String, zipcode: String, user: Long)

case class UpdateCurrentUserAddress(street: String, city: String, zipcode: String)
