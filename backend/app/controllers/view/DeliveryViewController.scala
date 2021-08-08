package controllers.view

import java.text.SimpleDateFormat
import java.util.Date

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AnyContent, MessagesControllerComponents, _}
import repository.{CartRepository, DeliveryRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeliveryViewController @Inject()(cc: MessagesControllerComponents, deliveryRepository: DeliveryRepository, cartRepository: CartRepository)
                                      (implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val deliveryForm: Form[CreateDeliveryForm] = Form {
    mapping(
      "cart" -> longNumber,
      "deliveryTimestamp" -> date(pattern = "yyyy-MM-dd"),
      "isDelivered" -> boolean
    )(CreateDeliveryForm.apply)(CreateDeliveryForm.unapply)
  }

  val updateDeliveryForm: Form[UpdateDeliveryForm] = Form {
    mapping(
      "id" -> longNumber,
      "cart" -> longNumber,
      "deliveryTimestamp" -> date(pattern = "yyyy-MM-dd"),
      "isDelivered" -> boolean
    )(UpdateDeliveryForm.apply)(UpdateDeliveryForm.unapply)
  }

  val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val deliveries = deliveryRepository.list
    deliveries.map(p => Ok(views.html.delivery.deliveries(p)))
  }

  def findOne(id: Long): Action[AnyContent] = Action.async { implicit request =>
    deliveryRepository.getByIdOption(id)
      .map {
        case Some(value) => Ok(views.html.delivery.delivery(value))
        case None => Redirect(routes.DeliveryViewController.getAll)
      }
  }

  def add: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    cartRepository.list.map(carts =>
      Ok(views.html.delivery.deliveryadd(deliveryForm, carts))
    )
  }

  def addHandle = Action.async { implicit request =>
    cartRepository.list.flatMap(carts => {
      deliveryForm.bindFromRequest.fold(
        errorForm => Future.successful(BadRequest(views.html.delivery.deliveryadd(errorForm, carts))),
        delivery => deliveryRepository.create(delivery.cart, dateFormat.format(delivery.deliveryTimestamp), delivery.isDelivered)
          .map(_ => Redirect(routes.DeliveryViewController.add).flashing("success" -> "delivery created"))
      )
    })
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    deliveryRepository.delete(id)
      .map(_ => Redirect(controllers.view.routes.DeliveryViewController.getAll))
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    cartRepository.list.flatMap(carts => {
      deliveryRepository.getById(id).map(delivery => {
        val deliveryForm = updateDeliveryForm.fill(UpdateDeliveryForm(id, delivery.cart.id, dateFormat.parse(delivery.deliveryTimestamp), delivery.isDelivered))
        Ok(views.html.delivery.deliveryupdate(deliveryForm, carts))
      })
    })
  }

  def updateHandle = Action.async { implicit request =>
    cartRepository.list.flatMap(carts => {
      updateDeliveryForm.bindFromRequest.fold(
        errorForm => Future.successful(BadRequest(views.html.delivery.deliveryupdate(errorForm, carts))),
        delivery => deliveryRepository.update(delivery.id, delivery.cart, dateFormat.format(delivery.deliveryTimestamp), delivery.isDelivered)
          .map(_ => Redirect(routes.DeliveryViewController.update(delivery.id)).flashing("success" -> "delivery updated"))
      )
    })
  }

}

case class CreateDeliveryForm(cart: Long, deliveryTimestamp: Date, isDelivered: Boolean)

case class UpdateDeliveryForm(id: Long, cart: Long, deliveryTimestamp: Date, isDelivered: Boolean)