package repository

import controllers.dto.StockDto
import models.Stock
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider, val productRepository: ProductRepository, val categoryRepository: CategoryRepository)
                               (implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val stockTable = TableQuery[StockTable]

  import productRepository.ProductTable

  private val productTable = TableQuery[ProductTable]

  import categoryRepository.CategoryTable

  private val categoryTable = TableQuery[CategoryTable]

  def create(product: Long, amount: Long): Future[Stock] = db.run {
    (stockTable.map(c => (c.product, c.amount))
      returning stockTable.map(_.id)
      into { case ((product, amount), id) => Stock(id, product, amount) }
      ) += (product, amount)
  }

  def list: Future[List[StockDto]] = db.run {
    val joinQuery = for {
      ((s, p), c) <- stockTable join productTable on (_.product === _.id) join categoryTable on (_._2.category === _.id)
    } yield (s, p, c)
    joinQuery.result
      .map(_.toStream
        .map(data => StockDto(data._1, data._2, data._3))
        .toList)
  }

  def getById(id: Long): Future[StockDto] = db.run {
    val joinQuery = for {
      ((s, p), c) <- stockTable join productTable on (_.product === _.id) join categoryTable on (_._2.category === _.id)
    } yield (s, p, c)
    joinQuery.filter(_._1.id === id).result.head
      .map(data => StockDto(data._1, data._2, data._3))
  }

  def getByIdOption(id: Long): Future[Option[StockDto]] = db.run {
    val joinQuery = for {
      ((s, p), c) <- stockTable join productTable on (_.product === _.id) join categoryTable on (_._2.category === _.id)
    } yield (s, p, c)
    joinQuery.filter(_._1.id === id).result.headOption
      .map {
        case Some(value) => Some(StockDto(value._1, value._2, value._3))
      }
  }

  def delete(id: Long): Future[Unit] = db.run(stockTable.filter(_.id === id).delete.map(_ => ()))

  def update(id: Long, product: Long, amount: Long): Future[Unit] = {
    val newStock = Stock(id, product, amount)
    db.run(stockTable.filter(_.id === id).update(newStock).map(_ => ()))
  }

  def decreaseStockAmount(productId: Long, decreaseBy: Long): Future[Unit] = {
    db.run(stockTable
      .filter(_.product === productId)
      .result
      .headOption)
      .flatMap {
        case Some(stock) =>
          var amountAfterDecrease = 0L
          if (stock.amount > decreaseBy) {
            amountAfterDecrease = stock.amount - decreaseBy
          }
          update(stock.id, stock.product, amountAfterDecrease)
        case None => Future.successful(())
      }
  }

  class StockTable(tag: Tag) extends Table[Stock](tag, "stock") {
    def fkProduct = foreignKey("fk_product", product, productTable)(_.id)

    def * = (id, product, amount) <> ((Stock.apply _).tupled, Stock.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def product = column[Long]("product")

    def amount = column[Long]("amount")
  }
}