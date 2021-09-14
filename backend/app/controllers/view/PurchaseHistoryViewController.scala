package controllers.view

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import repository.{CartRepository, PaymentMethodRepository, PurchaseHistoryRepository, UserAddressRepository}

import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PurchaseHistoryViewController @Inject()(cc: MessagesControllerComponents, purchaseHistoryRepository: PurchaseHistoryRepository, cartRepository: CartRepository,
                                              paymentMethodRepository: PaymentMethodRepository, userAddressRepository: UserAddressRepository)
                                             (implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val purchaseHistoryForm: Form[CreatePurchaseHistoryForm] = Form {
    mapping(
      "cart" -> longNumber,
      "paymentMethod" -> longNumber,
      "address" -> longNumber,
      "totalPrice" -> bigDecimal,
      "purchaseTimestamp" -> date(pattern = "yyyy-MM-dd")
    )(CreatePurchaseHistoryForm.apply)(CreatePurchaseHistoryForm.unapply)
  }

  val updatePurchaseHistoryForm: Form[UpdatePurchaseHistoryForm] = Form {
    mapping(
      "id" -> longNumber,
      "cart" -> longNumber,
      "paymentMethod" -> longNumber,
      "address" -> longNumber,
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
    cartRepository.list.flatMap(carts =>
      paymentMethodRepository.list.flatMap(paymentMethods =>
        userAddressRepository.list.map(addresses =>
          Ok(views.html.purchasehistory.purchasehistoryadd(purchaseHistoryForm, carts, paymentMethods, addresses))
        ))
    )
  }

  def addHandle = Action.async { implicit request =>
    cartRepository.list.flatMap(carts =>
      paymentMethodRepository.list.flatMap(paymentMethods =>
        userAddressRepository.list.flatMap(addresses =>
          purchaseHistoryForm.bindFromRequest.fold(
            errorForm => Future.successful(BadRequest(views.html.purchasehistory.purchasehistoryadd(errorForm, carts, paymentMethods, addresses))),
            history => purchaseHistoryRepository.create(history.cart, history.paymentMethod, history.address, history.totalPrice.doubleValue(), dateFormat.format(history.purchaseTimestamp))
              .map(_ => Redirect(routes.PurchaseHistoryViewController.add).flashing("success" -> "purchase history created"))
          )
        )
      )
    )
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    purchaseHistoryRepository.delete(id)
      .map(_ => Redirect(controllers.view.routes.PurchaseHistoryViewController.getAll))
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    cartRepository.list.flatMap(carts => {
      paymentMethodRepository.list.flatMap(paymentMethods =>
        userAddressRepository.list.flatMap(addresses =>
          purchaseHistoryRepository.getById(id).map(history => {
            val purchaseHistoryForm = updatePurchaseHistoryForm.fill(UpdatePurchaseHistoryForm(id, history.cart.id, history.paymentMethod.id, history.address.id, BigDecimal(history.totalPrice), dateFormat.parse(history.purchaseTimestamp)))
            Ok(views.html.purchasehistory.purchasehistoryupdate(purchaseHistoryForm, carts, paymentMethods, addresses))
          })
        )
      )
    })
  }

  def updateHandle = Action.async { implicit request =>
    cartRepository.list.flatMap(carts =>
      paymentMethodRepository.list.flatMap(paymentMethods =>
        userAddressRepository.list.flatMap(addresses =>
          updatePurchaseHistoryForm.bindFromRequest.fold(
            errorForm => Future.successful(BadRequest(views.html.purchasehistory.purchasehistoryupdate(errorForm, carts, paymentMethods, addresses))),
            history => purchaseHistoryRepository.update(history.id, history.cart, history.paymentMethod, history.address, history.totalPrice.doubleValue(), dateFormat.format(history.purchaseTimestamp))
              .map(_ => Redirect(routes.PurchaseHistoryViewController.update(history.id)).flashing("success" -> "purchase history updated"))
          )
        )
      )
    )
  }

}

case class CreatePurchaseHistoryForm(cart: Long, paymentMethod: Long, address: Long, totalPrice: BigDecimal, purchaseTimestamp: Date)

case class UpdatePurchaseHistoryForm(id: Long, cart: Long, paymentMethod: Long, address: Long, totalPrice: BigDecimal, purchaseTimestamp: Date)