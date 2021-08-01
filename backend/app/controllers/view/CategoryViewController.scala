package controllers.view

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AnyContent, MessagesControllerComponents, _}
import repository.CategoryRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryViewController @Inject()(cc: MessagesControllerComponents, categoryRepository: CategoryRepository)
                                      (implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val categoryForm: Form[CreateCategoryForm] = Form {
    mapping(
      "name" -> nonEmptyText
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
  }

  val updateCategoryForm: Form[UpdateCategoryForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText
    )(UpdateCategoryForm.apply)(UpdateCategoryForm.unapply)
  }

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val categories = categoryRepository.list()
    categories.map(p => Ok(views.html.category.categories(p)))
  }

  def findOne(id: Long): Action[AnyContent] = Action.async { implicit request =>
    categoryRepository.getByIdOption(id)
      .map {
        case Some(value) => Ok(views.html.category.category(value))
        case None => Redirect(routes.CategoryViewController.getAll)
      }
  }

  def add: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.category.categoryadd(categoryForm))
  }

  def addHandle = Action.async { implicit request =>
    categoryForm.bindFromRequest.fold(
      errorForm => Future.successful(BadRequest(views.html.category.categoryadd(errorForm))),
      category => categoryRepository.create(category.name)
        .map(_ => Redirect(routes.CategoryViewController.add).flashing("success" -> "category created"))
    )
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    categoryRepository.delete(id)
      .map(_ => Redirect(controllers.view.routes.CategoryViewController.getAll))
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    categoryRepository.getById(id).map(category => {
      val categoryForm = updateCategoryForm.fill(UpdateCategoryForm(id, category.name))
      Ok(views.html.category.categoryupdate(categoryForm))
    })
  }

  def updateHandle = Action.async { implicit request =>
    updateCategoryForm.bindFromRequest.fold(
      errorForm => Future.successful(BadRequest(views.html.category.categoryupdate(errorForm))),
      category => categoryRepository.update(category.id, category.name)
        .map(_ => Redirect(routes.CategoryViewController.update(category.id)).flashing("success" -> "category updated"))
    )
  }

}

case class CreateCategoryForm(name: String)

case class UpdateCategoryForm(id: Long, name: String)