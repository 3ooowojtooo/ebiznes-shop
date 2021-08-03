package controllers.view

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AnyContent, MessagesControllerComponents, _}
import repository.{CartItemRepository, CartRepository, ProductRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CartItemViewController @Inject()(cc: MessagesControllerComponents, productRepository: ProductRepository, cartItemRepository: CartItemRepository, cartRepository: CartRepository)
                                      (implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val cartItemForm: Form[CreateCartItemForm] = Form {
    mapping(
      "cart" -> longNumber,
      "product" -> longNumber,
      "amount" -> longNumber(min = 1)
    )(CreateCartItemForm.apply)(CreateCartItemForm.unapply)
  }

  val updateCartItemForm: Form[UpdateCartItemForm] = Form {
    mapping(
      "id" -> longNumber,
      "cart" -> longNumber,
      "product" -> longNumber,
      "amount" -> longNumber(min = 1)
    )(UpdateCartItemForm.apply)(UpdateCartItemForm.unapply)
  }

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val items = cartItemRepository.list
    items.map(p => Ok(views.html.cartitem.cartitems(p)))
  }

  def findOne(id: Long): Action[AnyContent] = Action.async { implicit request =>
    cartItemRepository.getByIdOption(id)
      .map {
        case Some(value) => Ok(views.html.cartitem.cartitem(value))
        case None => Redirect(routes.CartItemViewController.getAll)
      }
  }

  def add: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    productRepository.list.flatMap(products =>
      cartRepository.list.map(carts =>
        Ok(views.html.cartitem.cartitemadd(cartItemForm, products, carts))
      ))
  }

  def addHandle = Action.async { implicit request =>
    productRepository.list.flatMap(products =>
      cartRepository.list.flatMap(carts => {
        cartItemForm.bindFromRequest.fold(
          errorForm => Future.successful(BadRequest(views.html.cartitem.cartitemadd(errorForm, products, carts))),
          cartItem => cartItemRepository.create(cartItem.product, cartItem.amount, cartItem.cart))
          .map(_ => Redirect(routes.CartItemViewController.add).flashing("success" -> "cart item created"))
      })
    )
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    cartItemRepository.delete(id)
      .map(_ => Redirect(controllers.view.routes.CartItemViewController.getAll))
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    productRepository.list.flatMap(products =>
      cartRepository.list.flatMap(carts =>
        cartItemRepository.getById(id).map(cartItem => {
          val cartItemForm = updateCartItemForm.fill(UpdateCartItemForm(id, cartItem.cart, cartItem.product, cartItem.amount))
          Ok(views.html.cartitem.cartitemupdate(cartItemForm, products, carts))
        })
      )
    )
  }

  def updateHandle = Action.async { implicit request =>
    productRepository.list.flatMap(products =>
      cartRepository.list.flatMap(carts =>
        updateCartItemForm.bindFromRequest.fold(
          errorForm => Future.successful(BadRequest(views.html.cartitem.cartitemupdate(errorForm, products, carts))),
          cartItem => cartItemRepository.update(cartItem.id, cartItem.product, cartItem.amount, cartItem.cart)
            .map(_ => Redirect(routes.CartItemViewController.update(cartItem.id)).flashing("success" -> "cart item updated"))
        )
      )
    )
  }
}

case class CreateCartItemForm(cart: Long, product: Long, amount: Long)

case class UpdateCartItemForm(id: Long, cart: Long, product: Long, amount: Long)