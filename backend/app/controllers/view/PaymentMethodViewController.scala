package controllers.view

import javax.inject.{Inject, Singleton}
import models.Product
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.mvc.{AnyContent, MessagesControllerComponents}
import repository.{CategoryRepository, PaymentMethodRepository, ProductRepository, UserRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentMethodViewController @Inject()(cc: MessagesControllerComponents, paymentMethodRepository: PaymentMethodRepository, userRepository: UserRepository)
                                     (implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val paymentMethodForm: Form[CreatePaymentMethodForm] = Form {
    mapping(
      "user" -> longNumber,
      "name" -> nonEmptyText
    )(CreatePaymentMethodForm.apply)(CreatePaymentMethodForm.unapply)
  }

  val updatePaymentMethodForm: Form[UpdatePaymentMethodForm] = Form {
    mapping(
      "id" -> longNumber,
      "user" -> longNumber,
      "name" -> nonEmptyText
    )(UpdatePaymentMethodForm.apply)(UpdatePaymentMethodForm.unapply)
  }

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val methods = paymentMethodRepository.list
    methods.map(p => Ok(views.html.paymentmethod.paymentmethods(p)))
  }

  def findOne(id : Long) : Action[AnyContent] = Action.async {implicit request =>
    paymentMethodRepository.getByIdOption(id)
      .map {
        case Some(value) => Ok(views.html.paymentmethod.paymentmethod(value))
        case None => Redirect(routes.PaymentMethodViewController.getAll)
      }
  }

  def add : Action[AnyContent] = Action.async {implicit request : MessagesRequest[AnyContent] =>
    userRepository.list.map(users =>
      Ok(views.html.paymentmethod.paymentmethodadd(paymentMethodForm, users))
    )
  }

  def addHandle = Action.async { implicit request =>
    userRepository.list.flatMap(users => {
      paymentMethodForm.bindFromRequest.fold(
        errorForm => Future.successful(BadRequest(views.html.paymentmethod.paymentmethodadd(errorForm, users))),
        method => paymentMethodRepository.create(method.user, method.name)
          .map(_ => Redirect(routes.PaymentMethodViewController.add).flashing("success" -> "payment method created"))
      )
    })
  }

  def delete(id : Long) : Action[AnyContent] = Action.async {
    paymentMethodRepository.delete(id)
      .map(_ => Redirect(controllers.view.routes.PaymentMethodViewController.getAll))
  }

  def update(id: Long) : Action[AnyContent] = Action.async {implicit request: MessagesRequest[AnyContent] =>
    userRepository.list.flatMap(users => {
      paymentMethodRepository.getById(id).map(method => {
        val paymentMethodForm = updatePaymentMethodForm.fill(UpdatePaymentMethodForm(id, method.user, method.name))
        Ok(views.html.paymentmethod.paymentmethodupdate(paymentMethodForm, users))
      })
    })
  }

  def updateHandle = Action.async {implicit request =>
    userRepository.list.flatMap(users => {
      updatePaymentMethodForm.bindFromRequest.fold(
        errorForm => Future.successful(BadRequest(views.html.paymentmethod.paymentmethodupdate(errorForm, users))),
        method => paymentMethodRepository.update(method.id, method.user, method.name)
          .map(_ => Redirect(routes.PaymentMethodViewController.update(method.id)).flashing("success" -> "payment method updated"))
      )
    })
  }

}

case class CreatePaymentMethodForm(user : Long, name : String)
case class UpdatePaymentMethodForm(id: Long, user : Long, name : String)