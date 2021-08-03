package repository

import javax.inject.{Inject, Singleton}
import models.CartItem
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CartItemRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider, val productRepository: ProductRepository, val cartRepository: CartRepository)
(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CartItemTable(tag : Tag) extends Table[CartItem](tag, "cart_item") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def product = column[Long]("product")
    def amount = column[Long]("amount")
    def cart = column[Long]("cart")
    def fk_product = foreignKey("fk_product", product, productTable)(_.id)
    def fk_cart = foreignKey("fk_cart", cart, cartTable)(_.id)
    def * = (id, cart, product, amount) <> ((CartItem.apply _).tupled, CartItem.unapply)
  }

  import productRepository.ProductTable
  import cartRepository.CartTable

  private val cartItemTable = TableQuery[CartItemTable]
  private val productTable = TableQuery[ProductTable]
  private val cartTable = TableQuery[CartTable]

  def create(product : Long, amount : Long, cart : Long) : Future[CartItem] = db.run {
    (cartItemTable.map(c => (c.product, c.amount, c.cart))
      returning cartItemTable.map(_.id)
      into {case ((product, amount, cart),id) => CartItem(id, cart, product, amount)}
      ) += (product, amount, cart)
  }

  def list : Future[Seq[CartItem]] = db.run {
    cartItemTable.result
  }

  def getById(id : Long) : Future[CartItem] = db.run {
    cartItemTable.filter(_.id === id).result.head
  }

  def getByIdOption(id : Long) : Future[Option[CartItem]] = db.run {
    cartItemTable.filter(_.id === id).result.headOption
  }

  def delete(id : Long) : Future[Unit] = db.run(cartItemTable.filter(_.id === id).delete.map(_ => ()))

  def update(id : Long, product : Long, amount : Long, cart : Long) : Future[Unit] = db.run {
    val newItem = CartItem(id, cart, product, amount)
    cartItemTable.filter(_.id === id).update(newItem).map(_ => ())
  }
}
