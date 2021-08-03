package controllers.view

import java.text.SimpleDateFormat
import java.util.Date

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AnyContent, MessagesControllerComponents, _}
import repository.{CartRepository, UserRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CartViewController @Inject()(cc: MessagesControllerComponents, cartRepository: CartRepository, userRepository: UserRepository)
                                  (implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val cartForm: Form[CreateCartForm] = Form {
    mapping(
      "createdTime" -> date(pattern = "yyyy-MM-dd"),
      "user" -> longNumber,
      "purchased" -> boolean
    )(CreateCartForm.apply)(CreateCartForm.unapply)
  }

  val updateCartForm: Form[UpdateCartForm] = Form {
    mapping(
      "id" -> longNumber,
      "createdTime" -> date,
      "user" -> longNumber,
      "purchased" -> boolean
    )(UpdateCartForm.apply)(UpdateCartForm.unapply)
  }

  val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val carts = cartRepository.list
    carts.map(p => Ok(views.html.cart.carts(p)))
  }

  def findOne(id: Long): Action[AnyContent] = Action.async { implicit request =>
    cartRepository.getByIdOption(id)
      .map {
        case Some(value) => Ok(views.html.cart.cart(value))
        case None => Redirect(routes.CartViewController.getAll)
      }
  }

  def add: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    userRepository.list.map(users =>
      Ok(views.html.cart.cartadd(cartForm, users))
    )
  }

  def addHandle = Action.async { implicit request =>
    userRepository.list.flatMap(users => {
      cartForm.bindFromRequest.fold(
        errorForm => Future.successful(BadRequest(views.html.cart.cartadd(errorForm, users))),
        cart => cartRepository.create(dateFormat.format(cart.createdTime), cart.user, cart.purchased)
          .map(_ => Redirect(routes.CartViewController.add).flashing("success" -> "cart created"))
      )
    })
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    cartRepository.delete(id)
      .map(_ => Redirect(controllers.view.routes.CartViewController.getAll))
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    userRepository.list.flatMap(users => {
      cartRepository.getById(id).map(cart => {
        val cartForm = updateCartForm.fill(UpdateCartForm(id, dateFormat.parse(cart.createdTime), cart.user, cart.purchased))
        Ok(views.html.cart.cartupdate(cartForm, users))
      })
    })
  }

  def updateHandle = Action.async { implicit request =>
    userRepository.list.flatMap(users => {
      updateCartForm.bindFromRequest.fold(
        errorForm => Future.successful(BadRequest(views.html.cart.cartupdate(errorForm, users))),
        cart => cartRepository.update(cart.id, dateFormat.format(cart.createdTime), cart.user, cart.purchased)
          .map(_ => Redirect(routes.CartViewController.update(cart.id)).flashing("success" -> "cart updated"))
      )
    })
  }

}

case class CreateCartForm(createdTime: Date, user: Long, purchased: Boolean)

case class UpdateCartForm(id: Long, createdTime: Date, user: Long, purchased: Boolean)