package repository

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.PasswordInfo
import controllers.dto.UserDto

import javax.inject.{Inject, Singleton}
import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.DBIOAction
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

@Singleton
class UserRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext, implicit val classTag: ClassTag[PasswordInfo])
extends IdentityService[User] {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  private val user = TableQuery[UserTable]

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = db.run {
    user.filter(_.providerId === loginInfo.providerID)
      .filter(_.providerKey === loginInfo.providerKey)
      .result
      .headOption
  }.map(_.map(User.apply))

  def create(email: String, providerId : String, providerKey : String): Future[UserDto] = {
    db.run(
      user.filter(_.providerId === providerId)
        .filter(_.providerKey === providerKey)
        .filter(_.email === email)
        .result
        .headOption
    ).flatMap {
      case Some(usr) => Future.successful(usr)
      case None => db.run(
        (user.map(u => (u.email, u.providerId, u.providerKey))
          returning user.map(_.id)
          into { case ((email, providerId, providerKey), id) => UserDto(id, email, providerId, providerKey) }
          ) += (email, providerId, providerKey)
      )
    }
  }


  /*
    def create(email: String, providerId : String, providerKey : String): Future[UserDto] = db.run {
      user.filter(_.providerId === providerId)
        .filter(_.providerKey === providerKey)
        .filter(_.email === email)
        .result
        .headOption
        .flatMap {
          case Some(x) => DBIOAction.successful(x)
          case None =>
            (user.map(u => (u.email, u.providerId, u.providerKey))
              returning user.map(_.id)
              into { case ((email, providerId, providerKey), id) => UserDto(id, email, providerId, providerKey) }
              ) += (email, providerId, providerKey)
        }
    }
  */

  def list: Future[List[UserDto]] = db.run {
    user.result
      .map(_.toStream
        .toList)
  }

  def getById(id: Long): Future[UserDto] = db.run {
    user.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[UserDto]] = db.run {
    user.filter(_.id === id).result.headOption
  }

  def delete(id: Long): Future[Unit] = db.run {
    user.filter(_.id === id).delete.map(_ => ())
  }

  def update(id: Long, email: String, providerId : String, providerKey : String): Future[Unit] = {
    val updatedUser = UserDto(id, email, providerId, providerKey)
    db.run(user.filter(_.id === id).update(updatedUser).map(_ => ()))
  }

  class UserTable(tag: Tag) extends Table[UserDto](tag, "user") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def email = column[String]("email")

    def providerId = column[String]("providerId")

    def providerKey = column[String]("providerKey")

    def * = (id, email, providerId, providerKey) <> (data => UserDto.apply(data._1, data._2, data._3, data._4), UserDto.unapply)
  }
}