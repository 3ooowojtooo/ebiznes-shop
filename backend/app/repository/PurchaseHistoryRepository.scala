package repository

import controllers.dto.{PurchaseHistoryDto, UserDto}

import javax.inject.Inject
import models.{Cart, PurchaseHistory}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Inject
class PurchaseHistoryRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider,
                                          val cartRepository: CartRepository, val userRepository: UserRepository,
                                          val cartItemRepository: CartItemRepository)
                                         (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  private val purchaseHistoryTable = TableQuery[PurchaseHistoryTable]

  import cartRepository.CartTable
  private val cartTable = TableQuery[CartTable]

  import userRepository.UserTable
  private val userTable = TableQuery[UserTable]

  import cartItemRepository.CartItemTable
  private val cartItemTable = TableQuery[CartItemTable]

  def create(cart: Long, totalPrice: Double, purchaseTimestamp: String): Future[PurchaseHistory] = db.run {
    (purchaseHistoryTable.map(c => (c.cart, c.totalPrice, c.purchaseTimestamp))
      returning purchaseHistoryTable.map(_.id)
      into { case ((cart, totalPrice, purchaseTimestamp), id) => PurchaseHistory(id, cart, totalPrice, purchaseTimestamp) }
      ) += (cart, totalPrice, purchaseTimestamp)
  }

  def list: Future[List[PurchaseHistoryDto]] = db.run {
    val joinQuery = for {
      ((h, c), u) <- purchaseHistoryTable join cartTable on (_.cart === _.id) join userTable on (_._2.user === _.id)
    } yield(h,c,u)
    joinQuery.result
      .map(_.toStream
      .map(data => PurchaseHistoryDto(data._1, data._2, data._3))
      .toList)
  }

  def getById(id: Long): Future[PurchaseHistoryDto] = db.run {
    val joinQuery = for {
      ((h, c), u) <- purchaseHistoryTable join cartTable on (_.cart === _.id) join userTable on (_._2.user === _.id)
    } yield(h,c,u)
    joinQuery.filter(_._1.id === id).result.head
      .map(data => PurchaseHistoryDto(data._1, data._2, data._3))
  }

  def getByIdOption(id: Long): Future[Option[PurchaseHistoryDto]] = db.run {
    val joinQuery = for {
      ((h, c), u) <- purchaseHistoryTable join cartTable on (_.cart === _.id) join userTable on (_._2.user === _.id)
    } yield (h,c,u)
    joinQuery.filter(_._1.id === id).result.headOption
      .map {
        case Some(value) => Some(PurchaseHistoryDto(value._1, value._2, value._3))
        case None => None
      }
  }

  def getUserHistory(userId : Long): Future[Seq[(PurchaseHistory, Cart)]] = db.run {
    val joinQuery = for {
      (h, c) <- purchaseHistoryTable join cartTable on (_.cart === _.id)
    } yield (h,c)
    joinQuery
      .filter(_._2.user === userId)
      .filter(_._2.purchased === true)
      .result
  }

  def delete(id: Long): Future[Unit] = db.run(purchaseHistoryTable.filter(_.id === id).delete.map(_ => ()))

  def update(id: Long, cart: Long, totalPrice: Double, purchaseTimestamp: String): Future[Unit] = {
    val newPurchaseHistory = PurchaseHistory(id, cart, totalPrice, purchaseTimestamp)
    db.run(purchaseHistoryTable.filter(_.id === id).update(newPurchaseHistory).map(_ => ()))
  }

  class PurchaseHistoryTable(tag: Tag) extends Table[PurchaseHistory](tag, "purchase_history") {
    def fk_cart = foreignKey("fk_cart", cart, cartTable)(_.id)

    def * = (id, cart, totalPrice, purchaseTimestamp) <> ((PurchaseHistory.apply _).tupled, PurchaseHistory.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def cart = column[Long]("cart")

    def totalPrice = column[Double]("totalPrice")

    def purchaseTimestamp = column[String]("purchase_timestamp")
  }
}
