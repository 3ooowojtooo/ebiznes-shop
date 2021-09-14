package repository

import controllers.dto.{PurchaseHistoryDto, UserDto}

import javax.inject.Inject
import models.{Cart, PaymentMethod, PurchaseHistory, UserAddress}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Inject
class PurchaseHistoryRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider,
                                          val cartRepository: CartRepository, val userRepository: UserRepository,
                                          val paymentMethodRepository: PaymentMethodRepository,
                                          val userAddressRepository: UserAddressRepository)
                                         (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  private val purchaseHistoryTable = TableQuery[PurchaseHistoryTable]

  import cartRepository.CartTable
  private val cartTable = TableQuery[CartTable]

  import userRepository.UserTable
  private val userTable = TableQuery[UserTable]

  import paymentMethodRepository.PaymentMethodTable
  private val paymentMethodTable = TableQuery[PaymentMethodTable]

  import userAddressRepository.UserAddressTable
  private val userAddressTable = TableQuery[UserAddressTable]

  def create(cart: Long, paymentMethod : Long, address : Long, totalPrice: Double, purchaseTimestamp: String): Future[PurchaseHistory] = db.run {
    (purchaseHistoryTable.map(c => (c.cart, c.paymentMethod, c.address, c.totalPrice, c.purchaseTimestamp))
      returning purchaseHistoryTable.map(_.id)
      into { case ((cart, paymentMethod, address, totalPrice, purchaseTimestamp), id) => PurchaseHistory(id, cart, paymentMethod, address, totalPrice, purchaseTimestamp) }
      ) += (cart, paymentMethod, address, totalPrice, purchaseTimestamp)
  }

  def list: Future[List[PurchaseHistoryDto]] = db.run {
    val joinQuery = for {
      ((((h, c), u), p), a) <- purchaseHistoryTable join cartTable on (_.cart === _.id) join userTable on (_._2.user === _.id) join paymentMethodTable on (_._1._1.paymentMethod === _.id) join userAddressTable on (_._1._1._1.address === _.id)
    } yield(h,c,u,p,a)
    joinQuery.result
      .map(_.toStream
      .map(data => PurchaseHistoryDto(data._1, data._2, data._3, data._4, data._5))
      .toList)
  }

  def getById(id: Long): Future[PurchaseHistoryDto] = db.run {
    val joinQuery = for {
      ((((h, c), u), p), a) <- purchaseHistoryTable join cartTable on (_.cart === _.id) join userTable on (_._2.user === _.id) join paymentMethodTable on (_._1._1.paymentMethod === _.id) join userAddressTable on (_._1._1._1.address === _.id)
    } yield(h,c,u,p,a)
    joinQuery.filter(_._1.id === id).result.head
      .map(data => PurchaseHistoryDto(data._1, data._2, data._3, data._4, data._5))
  }

  def getByIdOption(id: Long): Future[Option[PurchaseHistoryDto]] = db.run {
    val joinQuery = for {
      ((((h, c), u), p), a) <- purchaseHistoryTable join cartTable on (_.cart === _.id) join userTable on (_._2.user === _.id) join paymentMethodTable on (_._1._1.paymentMethod === _.id) join userAddressTable on (_._1._1._1.address === _.id)
    } yield (h,c,u,p,a)
    joinQuery.filter(_._1.id === id).result.headOption
      .map {
        case Some(data) => Some(PurchaseHistoryDto(data._1, data._2, data._3, data._4, data._5))
        case None => None
      }
  }

  def getUserHistory(userId : Long): Future[Seq[(PurchaseHistory, Cart, PaymentMethod, UserAddress)]] = db.run {
    val joinQuery = for {
      (((h,c),p),a) <- purchaseHistoryTable join cartTable on (_.cart === _.id) join paymentMethodTable on (_._1.paymentMethod === _.id) join userAddressTable on (_._1._1.address === _.id)
    } yield (h,c,p,a)
    joinQuery
      .filter(_._2.user === userId)
      .filter(_._2.purchased === true)
      .result
  }

  def delete(id: Long): Future[Unit] = db.run(purchaseHistoryTable.filter(_.id === id).delete.map(_ => ()))

  def update(id: Long, cart: Long, paymentMethod : Long, address : Long, totalPrice: Double, purchaseTimestamp: String): Future[Unit] = {
    val newPurchaseHistory = PurchaseHistory(id, cart, paymentMethod, address, totalPrice, purchaseTimestamp)
    db.run(purchaseHistoryTable.filter(_.id === id).update(newPurchaseHistory).map(_ => ()))
  }

  class PurchaseHistoryTable(tag: Tag) extends Table[PurchaseHistory](tag, "purchase_history") {
    def fkCart = foreignKey("fk_cart", cart, cartTable)(_.id)
    def fkPaymentMethod = foreignKey("fk_payment_method", paymentMethod, paymentMethodTable)(_.id)
    def fkAddress = foreignKey("fk_address", cart, userAddressTable)(_.id)

    def * = (id, cart, paymentMethod, address, totalPrice, purchaseTimestamp) <> ((PurchaseHistory.apply _).tupled, PurchaseHistory.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def cart = column[Long]("cart")

    def paymentMethod = column[Long]("payment_method")

    def address = column[Long]("address")

    def totalPrice = column[Double]("totalPrice")

    def purchaseTimestamp = column[String]("purchase_timestamp")
  }
}
