package repository

import javax.inject.{Inject, Singleton}
import models.{Stock, UserAddress}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAddressRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider, val userRepository: UserRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class UserAddressTable(tag: Tag) extends Table[UserAddress](tag, "user_address") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def street = column[String]("street")
    def city = column[String]("city")
    def zipcode = column[String]("zipcode")
    def user = column[Long]("user")
    def fk_user = foreignKey("fk_user", user, userTable)(_.id)
    def * = (id, street, city, zipcode, user) <> ((UserAddress.apply _).tupled, UserAddress.unapply)
  }

  import userRepository.UserTable

  private val userAddressTable = TableQuery[UserAddressTable]
  private val userTable = TableQuery[UserTable]

  def create(street : String, city : String, zipcode : String, user : Long): Future[UserAddress] = db.run {
    (userAddressTable.map(c => (c.street, c.city, c.zipcode, c.user))
      returning userAddressTable.map(_.id)
      into {case ((street, city, zipcode, user),id) => UserAddress(id, street, city, zipcode, user)}
      ) += (street, city, zipcode, user)
  }

  def list: Future[Seq[UserAddress]] = db.run {
    userAddressTable.result
  }

  def getById(id : Long) : Future[UserAddress] = db.run {
    userAddressTable.filter(_.id === id).result.head
  }

  def getByIdOption(id : Long) : Future[Option[UserAddress]] = db.run {
    userAddressTable.filter(_.id === id).result.headOption
  }

  def delete(id : Long) : Future[Unit] = db.run(userAddressTable.filter(_.id === id).delete.map(_ => ()))

  def update(id : Long, street : String, city : String, zipcode : String, user : Long) : Future[Unit] = {
    val newAddress = UserAddress(id, street, city, zipcode, user)
    db.run(userAddressTable.filter(_.id === id).update(newAddress).map(_ => ()))
  }
}