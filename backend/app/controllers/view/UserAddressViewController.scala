package controllers.view

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AnyContent, MessagesControllerComponents, _}
import repository.{UserAddressRepository, UserRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAddressViewController @Inject()(cc: MessagesControllerComponents, userAddressRepository: UserAddressRepository, userRepository: UserRepository)
                                         (implicit val ec: ExecutionContext) extends MessagesAbstractController(cc) {

  val userAddressForm: Form[CreateUserAddressForm] = Form {
    mapping(
      "street" -> nonEmptyText,
      "city" -> nonEmptyText,
      "zipcode" -> nonEmptyText,
      "user" -> longNumber
    )(CreateUserAddressForm.apply)(CreateUserAddressForm.unapply)
  }

  val updateUserAddressForm: Form[UpdateUserAddressForm] = Form {
    mapping(
      "id" -> longNumber,
      "street" -> nonEmptyText,
      "city" -> nonEmptyText,
      "zipcode" -> nonEmptyText,
      "user" -> longNumber
    )(UpdateUserAddressForm.apply)(UpdateUserAddressForm.unapply)
  }

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val userAddresses = userAddressRepository.list
    userAddresses.map(p => Ok(views.html.useraddress.useraddresses(p)))
  }

  def findOne(id: Long): Action[AnyContent] = Action.async { implicit request =>
    userAddressRepository.getByIdOption(id)
      .map {
        case Some(value) => Ok(views.html.useraddress.useraddress(value))
        case None => Redirect(routes.UserAddressViewController.getAll)
      }
  }

  def add: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    userRepository.list.map(users =>
      Ok(views.html.useraddress.useraddressadd(userAddressForm, users))
    )
  }

  def addHandle = Action.async { implicit request =>
    userRepository.list.flatMap(users => {
      userAddressForm.bindFromRequest.fold(
        errorForm => Future.successful(BadRequest(views.html.useraddress.useraddressadd(errorForm, users))),
        userAddress => userAddressRepository.create(userAddress.street, userAddress.city, userAddress.zipcode, userAddress.user)
          .map(_ => Redirect(routes.UserAddressViewController.add).flashing("success" -> "user address created"))
      )
    })
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    userAddressRepository.delete(id)
      .map(_ => Redirect(controllers.view.routes.UserAddressViewController.getAll))
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    userRepository.list.flatMap(users => {
      userAddressRepository.getById(id).map(userAddress => {
        val userAddressForm = updateUserAddressForm.fill(UpdateUserAddressForm(id, userAddress.street, userAddress.city, userAddress.zipcode, userAddress.user))
        Ok(views.html.useraddress.useraddressupdate(userAddressForm, users))
      })
    })
  }

  def updateHandle = Action.async { implicit request =>
    userRepository.list.flatMap(users => {
      updateUserAddressForm.bindFromRequest.fold(
        errorForm => Future.successful(BadRequest(views.html.useraddress.useraddressupdate(errorForm, users))),
        userAddress => userAddressRepository.update(userAddress.id, userAddress.street, userAddress.city, userAddress.zipcode, userAddress.user)
          .map(_ => Redirect(routes.UserAddressViewController.update(userAddress.id)).flashing("success" -> "user address updated"))
      )
    })
  }

}

case class CreateUserAddressForm(street: String, city: String, zipcode: String, user: Long)

case class UpdateUserAddressForm(id: Long, street: String, city: String, zipcode: String, user: Long)