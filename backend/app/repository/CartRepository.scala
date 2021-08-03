package repository

import javax.inject.{Inject, Singleton}
import models.Cart
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CartRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider, val userRepository: UserRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  private val cartTable = TableQuery[CartTable]

  import userRepository.UserTable
  private val userTable = TableQuery[UserTable]

  def create(created_time: String, user: Long, purchased: Boolean): Future[Cart] = db.run {
    (cartTable.map(c => (c.created_time, c.user, c.purchased))
      returning cartTable.map(_.id)
      into { case ((created_time, user, purchased), id) => Cart(id, created_time, user, purchased) }
      ) += (created_time, user, purchased)
  }

  def list: Future[Seq[Cart]] = db.run {
    cartTable.result
  }

  def getById(id: Long): Future[Cart] = db.run {
    cartTable.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Cart]] = db.run {
    cartTable.filter(_.id === id).result.headOption
  }

  def delete(id: Long): Future[Unit] = db.run(cartTable.filter(_.id === id).delete.map(_ => ()))

  def update(id: Long, created_time: String, user: Long, purchased: Boolean): Future[Unit] = {
    val newCart = Cart(id, created_time, user, purchased)
    db.run(cartTable.filter(_.id === id).update(newCart).map(_ => ()))
  }

  class CartTable(tag: Tag) extends Table[Cart](tag, "cart") {
    def fk_user = foreignKey("fk_user", user, userTable)(_.id)

    def user = column[Long]("user")

    def * = (id, created_time, user, purchased) <> ((Cart.apply _).tupled, Cart.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def created_time = column[String]("created_time")

    def purchased = column[Boolean]("purchased")
  }
}