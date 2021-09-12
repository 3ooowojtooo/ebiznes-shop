package repository

import controllers.dto.CartItemDto
import models.CartItem
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CartItemRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider, val productRepository: ProductRepository, val cartRepository: CartRepository,
                                   val userRepository: UserRepository, val categoryRepository: CategoryRepository)
                                  (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val cartItemTable = TableQuery[CartItemTable]

  import cartRepository.CartTable
  import categoryRepository.CategoryTable
  import productRepository.ProductTable
  import userRepository.UserTable

  private val productTable = TableQuery[ProductTable]
  private val cartTable = TableQuery[CartTable]
  private val userTable = TableQuery[UserTable]
  private val categoryTable = TableQuery[CategoryTable]

  def create(product: Long, amount: Long, cart: Long): Future[CartItem] = db.run {
    (cartItemTable.map(c => (c.product, c.amount, c.cart))
      returning cartItemTable.map(_.id)
      into { case ((product, amount, cart), id) => CartItem(id, cart, product, amount) }
      ) += (product, amount, cart)
  }

  def list: Future[Seq[CartItemDto]] = db.run {
    val joinQuery = for {
      ((((item, prod), cat), cart), user) <- cartItemTable join
        productTable on (_.product === _.id) join
        categoryTable on (_._2.category === _.id) join
        cartTable on (_._1._1.cart === _.id) join
        userTable on (_._2.user === _.id)
    } yield (item, prod, cat, cart, user)
    joinQuery.result
      .map(_.toStream
        .map(data => CartItemDto(data._1, data._2, data._4, data._5, data._3))
        .toList)
  }

  def listByCartId(cartId: Long): Future[Seq[CartItemDto]] = db.run {
    val joinQuery = for {
      ((((item, prod), cat), cart), user) <- cartItemTable join
        productTable on (_.product === _.id) join
        categoryTable on (_._2.category === _.id) join
        cartTable on (_._1._1.cart === _.id) join
        userTable on (_._2.user === _.id)
    } yield (item, prod, cat, cart, user)
    joinQuery
      .filter(_._4.id === cartId)
      .result
      .map(_.toStream
        .map(data => CartItemDto(data._1, data._2, data._4, data._5, data._3))
        .toList)
  }

  def getById(id: Long): Future[CartItemDto] = db.run {
    val joinQuery = for {
      ((((item, prod), cat), cart), user) <- cartItemTable join
        productTable on (_.product === _.id) join
        categoryTable on (_._2.category === _.id) join
        cartTable on (_._1._1.cart === _.id) join
        userTable on (_._2.user === _.id)
    } yield (item, prod, cat, cart, user)
    joinQuery.filter(_._1.id === id).result.head
      .map(data => CartItemDto(data._1, data._2, data._4, data._5, data._3))
  }

  def getByIdOption(id: Long): Future[Option[CartItemDto]] = db.run {
    val joinQuery = for {
      ((((item, prod), cat), cart), user) <- cartItemTable join
        productTable on (_.product === _.id) join
        categoryTable on (_._2.category === _.id) join
        cartTable on (_._1._1.cart === _.id) join
        userTable on (_._2.user === _.id)
    } yield (item, prod, cat, cart, user)
    joinQuery.filter(_._1.id === id).result.headOption
      .map {
        case Some(value) => Some(CartItemDto(value._1, value._2, value._4, value._5, value._3))
        case None => None
      }
  }

  def delete(id: Long): Future[Unit] = db.run(cartItemTable.filter(_.id === id).delete.map(_ => ()))

  def update(id: Long, product: Long, amount: Long, cart: Long): Future[Unit] = db.run {
    val newItem = CartItem(id, cart, product, amount)
    cartItemTable.filter(_.id === id).update(newItem).map(_ => ())
  }

  class CartItemTable(tag: Tag) extends Table[CartItem](tag, "cart_item") {
    def fk_product = foreignKey("fk_product", product, productTable)(_.id)

    def product = column[Long]("product")

    def fk_cart = foreignKey("fk_cart", cart, cartTable)(_.id)

    def cart = column[Long]("cart")

    def * = (id, cart, product, amount) <> ((CartItem.apply _).tupled, CartItem.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def amount = column[Long]("amount")
  }
}
