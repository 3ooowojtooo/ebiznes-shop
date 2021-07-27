package repository

import javax.inject.Inject
import models.PurchaseHistory
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Inject
class PurchaseHistoryRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider, val cartRepository: CartRepository)(implicit ec: ExecutionContext){
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class PurchaseHistoryTable(tag : Tag) extends Table[PurchaseHistory](tag, "purchase_history") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def cart = column[Long]("cart")
    def totalPrice = column[Double]("totalPrice")
    def purchaseTimestamp = column[String]("purchase_timestamp")
    def fk_cart = foreignKey("fk_cart", cart, cartTable)(_.id)
    def * = (id, cart, totalPrice, purchaseTimestamp) <> ((PurchaseHistory.apply _).tupled, PurchaseHistory.unapply)
  }

  import cartRepository.CartTable

  private val purchaseHistoryTable = TableQuery[PurchaseHistoryTable]
  private val cartTable = TableQuery[CartTable]

  def create(cart : Long, totalPrice : Double, purchaseTimestamp : String): Future[PurchaseHistory] = db.run {
    (purchaseHistoryTable.map(c => (c.cart, c.totalPrice, c.purchaseTimestamp))
      returning purchaseHistoryTable.map(_.id)
      into {case ((cart, totalPrice, purchaseTimestamp),id) => PurchaseHistory(id, cart, totalPrice, purchaseTimestamp)}
      ) += (cart, totalPrice, purchaseTimestamp)
  }

  def list: Future[Seq[PurchaseHistory]] = db.run {
    purchaseHistoryTable.result
  }

  def getByIdOption(id : Long) : Future[Option[PurchaseHistory]] = db.run {
    purchaseHistoryTable.filter(_.id === id).result.headOption
  }

  def delete(id : Long) : Future[Unit] = db.run(purchaseHistoryTable.filter(_.id === id).delete.map(_ => ()))

  def update(id : Long, cart : Long, totalPrice : Double, purchaseTimestamp : String) : Future[Unit] = {
    val newPurchaseHistory = PurchaseHistory(id, cart, totalPrice, purchaseTimestamp)
    db.run(purchaseHistoryTable.filter(_.id === id).update(newPurchaseHistory).map(_ => ()))
  }
}
