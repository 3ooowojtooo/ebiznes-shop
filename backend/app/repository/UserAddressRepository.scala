package repository

import controllers.dto.UserAddressDto
import models.UserAddress
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAddressRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider, val userRepository: UserRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val userAddressTable = TableQuery[UserAddressTable]

  import userRepository.UserTable

  private val userTable = TableQuery[UserTable]

  def create(street: String, city: String, zipcode: String, user: Long): Future[UserAddress] = db.run {
    (userAddressTable.map(c => (c.street, c.city, c.zipcode, c.user))
      returning userAddressTable.map(_.id)
      into { case ((street, city, zipcode, user), id) => UserAddress(id, street, city, zipcode, user) }
      ) += (street, city, zipcode, user)
  }

  def list: Future[List[UserAddressDto]] = db.run {
    val joinQuery = for {
      (a, u) <- userAddressTable join userTable on (_.user === _.id)
    } yield (a, u)
    joinQuery.result
      .map(_.toStream
        .map(data => UserAddressDto(data._1, data._2))
        .toList)
  }

  def getById(id: Long): Future[UserAddressDto] = db.run {
    val joinQuery = for {
      (a, u) <- userAddressTable join userTable on (_.user === _.id)
    } yield (a, u)
    joinQuery.filter(_._1.id === id).result.head
      .map(data => UserAddressDto(data._1, data._2))
  }

  def getByIdOption(id: Long): Future[Option[UserAddressDto]] = db.run {
    val joinQuery = for {
      (a, u) <- userAddressTable join userTable on (_.user === _.id)
    } yield (a, u)
    joinQuery.filter(_._1.id === id).result.headOption
      .map {
        case Some(value) => Some(UserAddressDto(value._1, value._2))
        case None => None
      }
  }

  def delete(id: Long): Future[Unit] = db.run(userAddressTable.filter(_.id === id).delete.map(_ => ()))

  def update(id: Long, street: String, city: String, zipcode: String, user: Long): Future[Unit] = {
    val newAddress = UserAddress(id, street, city, zipcode, user)
    db.run(userAddressTable.filter(_.id === id).update(newAddress).map(_ => ()))
  }

  def getUserAddresses(userId: Long): Future[Seq[UserAddress]] = db.run {
    userAddressTable
      .filter(_.user === userId)
      .result
  }

  class UserAddressTable(tag: Tag) extends Table[UserAddress](tag, "user_address") {
    def fk_user = foreignKey("fk_user", user, userTable)(_.id)

    def * = (id, street, city, zipcode, user) <> ((UserAddress.apply _).tupled, UserAddress.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def street = column[String]("street")

    def city = column[String]("city")

    def zipcode = column[String]("zipcode")

    def user = column[Long]("user")
  }
}