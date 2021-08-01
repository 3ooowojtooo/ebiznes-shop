package controllers.view

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AnyContent, MessagesControllerComponents, _}
import repository.{CategoryRepository, UserRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserViewController @Inject()(cc: MessagesControllerComponents, userRepository: UserRepository)
                                      (implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "age" -> longNumber(min = 1)
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  val updateUserForm: Form[UpdateUserForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "age" -> longNumber(min = 0),
    )(UpdateUserForm.apply)(UpdateUserForm.unapply)
  }

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val users = userRepository.list
    users.map(p => Ok(views.html.user.users(p)))
  }

  def findOne(id: Long): Action[AnyContent] = Action.async { implicit request =>
    userRepository.getByIdOption(id)
      .map {
        case Some(value) => Ok(views.html.user.user(value))
        case None => Redirect(routes.UserViewController.getAll)
      }
  }

  def add: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.user.useradd(userForm))
  }

  def addHandle = Action.async { implicit request =>
    userForm.bindFromRequest.fold(
      errorForm => Future.successful(BadRequest(views.html.user.useradd(errorForm))),
      user => userRepository.create(user.name, user.age)
        .map(_ => Redirect(routes.UserViewController.add).flashing("success" -> "user created"))
    )
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    userRepository.delete(id)
      .map(_ => Redirect(controllers.view.routes.UserViewController.getAll))
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    userRepository.getById(id).map(user => {
      val userForm = updateUserForm.fill(UpdateUserForm(id, user.name, user.age))
      Ok(views.html.user.userupdate(userForm))
    })
  }

  def updateHandle = Action.async { implicit request =>
    updateUserForm.bindFromRequest.fold(
      errorForm => Future.successful(BadRequest(views.html.user.userupdate(errorForm))),
      user => userRepository.update(user.id, user.name, user.age)
        .map(_ => Redirect(routes.UserViewController.update(user.id)).flashing("success" -> "user updated"))
    )
  }

}

case class CreateUserForm(name: String, age : Long)

case class UpdateUserForm(id: Long, name: String, age : Long)