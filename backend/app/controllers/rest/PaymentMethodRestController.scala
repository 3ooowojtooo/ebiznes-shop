package controllers.rest

import com.google.inject.Inject
import javax.inject.Singleton
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{AbstractController, ControllerComponents}
import repository.PaymentMethodRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentMethodRestController @Inject()(cc : ControllerComponents, paymentMethodRepository: PaymentMethodRepository)(implicit val executionContext : ExecutionContext)
extends AbstractController(cc) {

  implicit val createPaymentMethodFormatter : OFormat[CreatePaymentMethod] = Json.format[CreatePaymentMethod]
  implicit val updatePaymentMethodFormatter : OFormat[UpdatePaymentMethod] = Json.format[UpdatePaymentMethod]

  // GET /paymentmethod
  def getAll = Action.async {implicit request =>
    val paymentMethods = paymentMethodRepository.list
    paymentMethods.map(c => Ok(Json.toJson(c)))
  }

  // GET /paymentmethod/:id
  def findOne(id : Long) = Action.async {implicit request =>
    val paymentMethod = paymentMethodRepository.getByIdOption(id)
    paymentMethod.map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  // POST /paymentmethod
  def create = Action.async {implicit request =>
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
  def delete(id : Long) = Action.async {
    paymentMethodRepository.delete(id)
      .map(_ => Ok)
  }

  // PUT /paymentmethod/:id
  def update(id : Long) = Action.async {implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdatePaymentMethod](_).asOpt)
    requestBody match {
      case Some(itemToUpdate) =>
        paymentMethodRepository.update(id, itemToUpdate.user, itemToUpdate.name)
        .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }

}

case class CreatePaymentMethod(user : Long, name : String)
case class UpdatePaymentMethod(user : Long, name : String)