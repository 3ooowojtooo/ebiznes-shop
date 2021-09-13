package repository

import controllers.dto.CartDto
import models.Cart
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CartRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider, val userRepository: UserRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val cartTable = TableQuery[CartTable]

  import userRepository.UserTable

  private val userTable = TableQuery[UserTable]
  private val dateFormat = new SimpleDateFormat("yyyy-MM-dd")

  def create(created_time: String, user: Long, purchased: Boolean): Future[Cart] = db.run {
    (cartTable.map(c => (c.created_time, c.user, c.purchased))
      returning cartTable.map(_.id)
      into { case ((created_time, user, purchased), id) => Cart(id, created_time, user, purchased) }
      ) += (created_time, user, purchased)
  }

  def list: Future[List[CartDto]] = db.run {
    val joinQuery = for {
      (c, u) <- cartTable join userTable on (_.user === _.id)
    } yield (c, u)
    joinQuery.result
      .map(_.toStream
        .map(data => CartDto(data._1, data._2))
        .toList)
  }

  def getById(id: Long): Future[CartDto] = db.run {
    val joinQuery = for {
      (c, u) <- cartTable join userTable on (_.user === _.id)
    } yield (c, u)
    joinQuery.filter(_._1.id === id).result.head
      .map(data => CartDto(data._1, data._2))
  }

  def getByIdOption(id: Long): Future[Option[CartDto]] = db.run {
    val joinQuery = for {
      (c, u) <- cartTable join userTable on (_.user === _.id)
    } yield (c, u)
    joinQuery.filter(_._1.id === id).result.headOption
      .map {
        case Some(value) => Some(CartDto(value._1, value._2))
        case None => None
      }
  }

  def delete(id: Long): Future[Unit] = db.run(cartTable.filter(_.id === id).delete.map(_ => ()))

  def update(id: Long, created_time: String, user: Long, purchased: Boolean): Future[Unit] = {
    val newCart = Cart(id, created_time, user, purchased)
    db.run(cartTable.filter(_.id === id).update(newCart).map(_ => ()))
  }

  def getOrCreateUserCart(userId: Long): Future[Cart] = {
    db.run {
      val joinQuery = for {
        (c, u) <- cartTable join userTable on (_.user === _.id)
      } yield (c, u)
      joinQuery
        .filter(_._2.id === userId)
        .filter(_._1.purchased === false)
        .result
        .headOption
    }.flatMap {
      case Some((optCart, _)) => Future.successful(optCart)
      case None => create(dateFormat.format(new Date()), userId, purchased = false)
    }
  }

  def getUserCartId(userId : Long) : Future[Option[Long]] = db.run {
    val joinQuery = for {
      (c, u) <- cartTable join userTable on (_.user === _.id)
    } yield (c, u)
    joinQuery
      .filter(_._2.id === userId)
      .filter(_._1.purchased === false)
      .result
      .headOption
      .map {
        case Some((cartOpt, _)) => Some(cartOpt.id)
        case None => None
      }
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