package controllers.view

import javax.inject.{Inject, Singleton}
import models.{CategoryRepository, ProductRepository}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.mvc.{AnyContent, MessagesControllerComponents}

import scala.concurrent.ExecutionContext

@Singleton
class ProductViewController @Inject()(cc: MessagesControllerComponents, productRepository: ProductRepository, categoryRepository: CategoryRepository)
                                     (implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> number,
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  val updateProductForm: Form[UpdateProductForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> number
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val products = productRepository.list()
    products.map(p => Ok(views.html.products(p)))
  }

  def findOne(id : Long) : Action[AnyContent] = Action.async {implicit request =>
    productRepository.getByIdOption(id)
      .map {
        case Some(value) => Ok(value.description)
        case None => Redirect(routes.ProductViewController.getAll)
      }
  }

}

case class CreateProductForm(name: String, description: String, category: Int)
case class UpdateProductForm(id: Long, name: String, description: String, category: Int)