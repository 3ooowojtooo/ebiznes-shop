package controllers.view

import java.text.SimpleDateFormat
import java.util.Date

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AnyContent, MessagesControllerComponents, _}
import repository.{CartRepository, PurchaseHistoryRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PurchaseHistoryViewController @Inject()(cc: MessagesControllerComponents, purchaseHistoryRepository: PurchaseHistoryRepository, cartRepository: CartRepository)
                                             (implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val purchaseHistoryForm: Form[CreatePurchaseHistoryForm] = Form {
    mapping(
      "cart" -> longNumber,
      "totalPrice" -> bigDecimal,
      "purchaseTimestamp" -> date(pattern = "yyyy-MM-dd")
    )(CreatePurchaseHistoryForm.apply)(CreatePurchaseHistoryForm.unapply)
  }

  val updatePurchaseHistoryForm: Form[UpdatePurchaseHistoryForm] = Form {
    mapping(
      "id" -> longNumber,
      "cart" -> longNumber,
      "totalPrice" -> bigDecimal,
      "purchaseTimestamp" -> date(pattern = "yyyy-MM-dd")
    )(UpdatePurchaseHistoryForm.apply)(UpdatePurchaseHistoryForm.unapply)
  }

  val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val histories = purchaseHistoryRepository.list
    histories.map(p => Ok(views.html.purchasehistory.purchasehistories(p)))
  }

  def findOne(id: Long): Action[AnyContent] = Action.async { implicit request =>
    purchaseHistoryRepository.getByIdOption(id)
      .map {
        case Some(value) => Ok(views.html.purchasehistory.purchasehistory(value))
        case None => Redirect(routes.PurchaseHistoryViewController.getAll)
      }
  }

  def add: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    cartRepository.list.map(carts =>
      Ok(views.html.purchasehistory.purchasehistoryadd(purchaseHistoryForm, carts))
    )
  }

  def addHandle = Action.async { implicit request =>
    cartRepository.list.flatMap(carts => {
      purchaseHistoryForm.bindFromRequest.fold(
        errorForm => Future.successful(BadRequest(views.html.purchasehistory.purchasehistoryadd(errorForm, carts))),
        history => purchaseHistoryRepository.create(history.cart, history.totalPrice.doubleValue(), dateFormat.format(history.purchaseTimestamp))
          .map(_ => Redirect(routes.PurchaseHistoryViewController.add).flashing("success" -> "purchase history created"))
      )
    })
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    purchaseHistoryRepository.delete(id)
      .map(_ => Redirect(controllers.view.routes.PurchaseHistoryViewController.getAll))
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    cartRepository.list.flatMap(carts => {
      purchaseHistoryRepository.getById(id).map(history => {
        val purchaseHistoryForm = updatePurchaseHistoryForm.fill(UpdatePurchaseHistoryForm(id, history.cart, BigDecimal(history.totalPrice), dateFormat.parse(history.purchaseTimestamp)))
        Ok(views.html.purchasehistory.purchasehistoryupdate(purchaseHistoryForm, carts))
      })
    })
  }

  def updateHandle = Action.async { implicit request =>
    cartRepository.list.flatMap(carts => {
      updatePurchaseHistoryForm.bindFromRequest.fold(
        errorForm => Future.successful(BadRequest(views.html.purchasehistory.purchasehistoryupdate(errorForm, carts))),
        history => purchaseHistoryRepository.update(history.id, history.cart, history.totalPrice.doubleValue(), dateFormat.format(history.purchaseTimestamp))
          .map(_ => Redirect(routes.PurchaseHistoryViewController.update(history.id)).flashing("success" -> "purchase history updated"))
      )
    })
  }

}

case class CreatePurchaseHistoryForm(cart: Long, totalPrice: BigDecimal, purchaseTimestamp: Date)

case class UpdatePurchaseHistoryForm(id: Long, cart: Long, totalPrice: BigDecimal, purchaseTimestamp: Date)