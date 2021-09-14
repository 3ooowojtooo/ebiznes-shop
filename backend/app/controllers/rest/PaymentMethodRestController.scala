package controllers.rest

import com.google.inject.Inject
import controllers.auth.{AbstractAuthController, DefaultSilhouetteControllerComponents}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{Action, AnyContent}
import repository.PaymentMethodRepository

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentMethodRestController @Inject()(cc: DefaultSilhouetteControllerComponents, paymentMethodRepository: PaymentMethodRepository)(implicit val executionContext: ExecutionContext)
  extends AbstractAuthController(cc) {

  implicit val createPaymentMethodFormatter: OFormat[CreatePaymentMethod] = Json.format[CreatePaymentMethod]
  implicit val updatePaymentMethodFormatter: OFormat[UpdatePaymentMethod] = Json.format[UpdatePaymentMethod]
  implicit val updateUserPaymentMethodFormatter: OFormat[UpdateUserPaymentMethod] = Json.format[UpdateUserPaymentMethod]

  // GET /paymentmethod
  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val paymentMethods = paymentMethodRepository.list
    paymentMethods.map(c => Ok(Json.toJson(c)))
  }

  // GET /paymentmethod/:id
  def findOne(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val paymentMethod = paymentMethodRepository.getByIdOption(id)
    paymentMethod.map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  // POST /paymentmethod
  def create: Action[AnyContent] = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[CreatePaymentMethod](_).asOpt)
    requestBody match {
      case Some(newItem) =>
        paymentMethodRepository.create(newItem.user, newItem.name)
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

  // DELETE /paymentmethod/:id
  def delete(id: Long): Action[AnyContent] = Action.async {
    paymentMethodRepository.delete(id)
      .map(_ => Ok)
  }

  // PUT /paymentmethod/:id
  def update(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdatePaymentMethod](_).asOpt)
    requestBody match {
      case Some(itemToUpdate) =>
        paymentMethodRepository.update(id, itemToUpdate.user, itemToUpdate.name)
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

  // GET /currentpaymentmethod
  def getUserPaymentMethods: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    paymentMethodRepository.getUserPaymentMethods(request.identity.id)
      .map(methods => Ok(Json.toJson(methods)))
  }

  // PUT /currentpaymentmethod/:id
  def updateUserPaymentMethod(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdateUserPaymentMethod](_).asOpt)
    requestBody match {
      case Some(itemToUpdate) =>
        paymentMethodRepository.getByUserAndName(request.identity.id, itemToUpdate.name)
          .flatMap {
            case Some(_) => Future.successful(BadRequest("Payment method with specified name already exists"))
            case None => paymentMethodRepository.update(id, request.identity.id, itemToUpdate.name)
              .map(_ => Ok)
          }
      case None => Future(BadRequest)
    }
  }

  // POST /currentpaymentmethod
  def createUserPaymentMethod: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdateUserPaymentMethod](_).asOpt)
    requestBody match {
      case Some(itemToCreate) =>
        paymentMethodRepository.getByUserAndName(request.identity.id, itemToCreate.name)
          .flatMap {
            case Some(_) => Future.successful(BadRequest("Payment method with specified name already exists"))
            case None =>
              paymentMethodRepository.create(request.identity.id, itemToCreate.name)
                .map(_ => Ok)
          }
      case None => Future(BadRequest)
    }
  }

  // DELETE /currentpaymentmethod/:id
  def deleteUserPaymentMethod(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    paymentMethodRepository.delete(id, request.identity.id)
      .map(_ => Ok)
  }
}

case class CreatePaymentMethod(user: Long, name: String)

case class UpdatePaymentMethod(user: Long, name: String)

case class UpdateUserPaymentMethod(name: String)