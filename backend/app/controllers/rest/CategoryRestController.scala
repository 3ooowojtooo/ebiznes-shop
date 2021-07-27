package controllers.rest

import javax.inject.{Inject, Singleton}
import models.Product
import play.api.libs.json.{Json, OFormat, __}
import play.api.mvc._
import repository.CategoryRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryRestController @Inject()(cc: ControllerComponents, categoryRepository: CategoryRepository)(implicit val executionContext : ExecutionContext)
  extends AbstractController(cc) {

  // GET /category
  def getAll = Action.async {implicit request =>
    val categories = categoryRepository.list
    categories.map(p => Ok(Json.toJson(p)))
  }

  // GET /category/:id
  def findOne(id : Long) = Action.async {implicit request =>
    val category = categoryRepository.getByIdOption(id)
    category.map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }

  implicit val createCategoryFormatter: OFormat[CreateCategory] = Json.format[CreateCategory]

  // POST /category
  def create = Action.async {implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[CreateCategory](_).asOpt)
    requestBody match {
      case Some(newItem) =>
        categoryRepository.create(newItem.name)
        .map(p => Created(Json.toJson(p)))
      case None =>
        Future(BadRequest)
    }
  }

  // DELETE /category/id
  def delete(id : Long) = Action.async {
    categoryRepository.delete(id)
      .map(_ => Ok)
  }

  implicit val updateProductFormatter: OFormat[UpdateCategory] = Json.format[UpdateCategory]

  // PUT /category/id
  def update(id : Long) = Action.async { implicit request =>
    val requestBodyJson = request.body.asJson
    val requestBody = requestBodyJson.flatMap(Json.fromJson[UpdateCategory](_).asOpt)
    requestBody match {
      case Some(itemToUpdate) =>
        categoryRepository.update(id, itemToUpdate.name)
          .map(_ => Ok)
      case None => Future(BadRequest)
    }
  }
}

case class CreateCategory(name : String)
case class UpdateCategory(name : String)
