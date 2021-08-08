package controllers.view

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AnyContent, MessagesControllerComponents, _}
import repository.{CategoryRepository, ProductRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductViewController @Inject()(cc: MessagesControllerComponents, productRepository: ProductRepository, categoryRepository: CategoryRepository)
                                     (implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> longNumber,
      "price" -> bigDecimal
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  val updateProductForm: Form[UpdateProductForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> longNumber,
      "price" -> bigDecimal
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val products = productRepository.list
    products.map(p => Ok(views.html.product.products(p)))
  }

  def findOne(id: Long): Action[AnyContent] = Action.async { implicit request =>
    productRepository.getByIdOption(id)
      .map {
        case Some(value) => Ok(views.html.product.product(value))
        case None => Redirect(routes.ProductViewController.getAll)
      }
  }

  def add: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    categoryRepository.list().map(categories =>
      Ok(views.html.product.productadd(productForm, categories))
    )
  }

  def addHandle = Action.async { implicit request =>
    categoryRepository.list().flatMap(categories => {
      productForm.bindFromRequest.fold(
        errorForm => Future.successful(BadRequest(views.html.product.productadd(errorForm, categories))),
        product => productRepository.create(product.name, product.description, product.category, product.price.doubleValue())
          .map(_ => Redirect(routes.ProductViewController.add).flashing("success" -> "product created"))
      )
    })
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    productRepository.delete(id)
      .map(_ => Redirect(controllers.view.routes.ProductViewController.getAll))
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    categoryRepository.list().flatMap(categories => {
      productRepository.getById(id).map(product => {
        val productForm = updateProductForm.fill(UpdateProductForm(id, product.name, product.description, product.category.id, BigDecimal(product.price)))
        Ok(views.html.product.productupdate(productForm, categories))
      })
    })
  }

  def updateHandle = Action.async { implicit request =>
    categoryRepository.list().flatMap(categories => {
      updateProductForm.bindFromRequest.fold(
        errorForm => Future.successful(BadRequest(views.html.product.productupdate(errorForm, categories))),
        product => productRepository.update(product.id, product.name, product.description, product.category, product.price.doubleValue())
          .map(_ => Redirect(routes.ProductViewController.update(product.id)).flashing("success" -> "product updated"))
      )
    })
  }

}

case class CreateProductForm(name: String, description: String, category: Long, price: BigDecimal)

case class UpdateProductForm(id: Long, name: String, description: String, category: Long, price: BigDecimal)