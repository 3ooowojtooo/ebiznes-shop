package controllers.rest

import controllers.auth.{AbstractAuthController, DefaultSilhouetteControllerComponents}
import controllers.dto.UserDto

import javax.inject.{Inject, Singleton}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._
import repository.UserRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRestController @Inject()(cc: DefaultSilhouetteControllerComponents, userRepository: UserRepository)(implicit val executionContext: ExecutionContext)
  extends AbstractAuthController(cc) {

  // GET /user
  def getAll = Action.async { implicit request =>
    val users = userRepository.list
    users.map(p => Ok(Json.toJson(p)))
  }

  // GET /user/:id
  def findOne(id: Long) = Action.async { implicit request =>
    val user = userRepository.getByIdOption(id)
    user.map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  implicit val createUserFormatter: OFormat[CreateUser] = Json.format[CreateUser]

  // POST /user
  def create = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[CreateUser](_).asOpt)
    requestBody match {
      case Some(newItem) =>
        userRepository.create(newItem.email, newItem.providerId, newItem.providerKey)
          .map(p => Created(Json.toJson(p)))
      case None =>
        Future(BadRequest)
    }
  }

  // DELETE /user/id
  def delete(id: Long) = Action.async {
    userRepository.delete(id)
      .map(_ => Ok)
  }

  implicit val updateUserFormatter: OFormat[UpdateUser] = Json.format[UpdateUser]

  // PUT /user/id
  def update(id: Long) = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdateUser](_).asOpt)
    requestBody match {
      case Some(itemToUpdate) =>
        userRepository.update(id, itemToUpdate.email, itemToUpdate.providerId, itemToUpdate.providerKey)
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

  // GET /currentuser
  def getCurrentUser : Action[AnyContent] = silhouette.SecuredAction.async {implicit request =>
    Future.successful(
      Ok(Json.toJson(UserDto(request.identity)))
    )
  }
}

case class CreateUser(email: String, providerId : String, providerKey : String)

case class UpdateUser(email: String, providerId : String, providerKey : String)
