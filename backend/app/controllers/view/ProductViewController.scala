package controllers.view

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.mvc.{AnyContent, MessagesControllerComponents}
import repository.{CategoryRepository, ProductRepository}

import scala.concurrent.ExecutionContext

@Singleton
class ProductViewController @Inject()(cc: MessagesControllerComponents, productRepository: ProductRepository)
                                     (implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> longNumber,
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  val updateProductForm: Form[UpdateProductForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> longNumber
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val products = productRepository.list
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

case class CreateProductForm(name: String, description: String, category: Long)
case class UpdateProductForm(id: Long, name: String, description: String, category: Long)