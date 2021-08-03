package controllers.view

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AnyContent, MessagesControllerComponents, _}
import repository.{ProductRepository, StockRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockViewController @Inject()(cc: MessagesControllerComponents, stockRepository: StockRepository, productRepository: ProductRepository)
                                   (implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val stockForm: Form[CreateStockForm] = Form {
    mapping(
      "product" -> longNumber,
      "amount" -> longNumber(min = 0)
    )(CreateStockForm.apply)(CreateStockForm.unapply)
  }

  val updateStockForm: Form[UpdateStockForm] = Form {
    mapping(
      "id" -> longNumber,
      "product" -> longNumber,
      "amount" -> longNumber(min = 0)
    )(UpdateStockForm.apply)(UpdateStockForm.unapply)
  }

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val stocks = stockRepository.list
    stocks.map(p => Ok(views.html.stock.stocks(p)))
  }

  def findOne(id: Long): Action[AnyContent] = Action.async { implicit request =>
    stockRepository.getByIdOption(id)
      .map {
        case Some(value) => Ok(views.html.stock.stock(value))
        case None => Redirect(routes.StockViewController.getAll)
      }
  }

  def add: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    productRepository.list.map(products =>
      Ok(views.html.stock.stockadd(stockForm, products))
    )
  }

  def addHandle = Action.async { implicit request =>
    productRepository.list.flatMap(products => {
      stockForm.bindFromRequest.fold(
        errorForm => Future.successful(BadRequest(views.html.stock.stockadd(errorForm, products))),
        stock => stockRepository.create(stock.product, stock.amount)
          .map(_ => Redirect(routes.StockViewController.add).flashing("success" -> "stock created"))
      )
    })
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    stockRepository.delete(id)
      .map(_ => Redirect(controllers.view.routes.StockViewController.getAll))
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    productRepository.list.flatMap(products => {
      stockRepository.getById(id).map(stock => {
        val stockForm = updateStockForm.fill(UpdateStockForm(id, stock.product, stock.amount))
        Ok(views.html.stock.stockupdate(stockForm, products))
      })
    })
  }

  def updateHandle = Action.async { implicit request =>
    productRepository.list.flatMap(products => {
      updateStockForm.bindFromRequest.fold(
        errorForm => Future.successful(BadRequest(views.html.stock.stockupdate(errorForm, products))),
        stock => stockRepository.update(stock.id, stock.product, stock.amount)
          .map(_ => Redirect(routes.StockViewController.update(stock.id)).flashing("success" -> "stock updated"))
      )
    })
  }

}

case class CreateStockForm(product: Long, amount: Long)

case class UpdateStockForm(id: Long, product: Long, amount: Long)