package repository

import javax.inject.{Inject, Singleton}
import models.Stock
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider, val productRepository: ProductRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  private val stockTable = TableQuery[StockTable]

  import productRepository.ProductTable
  private val productTable = TableQuery[ProductTable]

  def create(product: Long, amount: Long): Future[Stock] = db.run {
    (stockTable.map(c => (c.product, c.amount))
      returning stockTable.map(_.id)
      into { case ((product, amount), id) => Stock(id, product, amount) }
      ) += (product, amount)
  }

  def list: Future[Seq[Stock]] = db.run {
    stockTable.result
  }

  def getById(id: Long): Future[Stock] = db.run {
    stockTable.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Stock]] = db.run {
    stockTable.filter(_.id === id).result.headOption
  }

  def delete(id: Long): Future[Unit] = db.run(stockTable.filter(_.id === id).delete.map(_ => ()))

  def update(id: Long, product: Long, amount: Long): Future[Unit] = {
    val newStock = Stock(id, product, amount)
    db.run(stockTable.filter(_.id === id).update(newStock).map(_ => ()))
  }

  class StockTable(tag: Tag) extends Table[Stock](tag, "stock") {
    def fk_product = foreignKey("fk_product", product, productTable)(_.id)

    def * = (id, product, amount) <> ((Stock.apply _).tupled, Stock.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def product = column[Long]("product")

    def amount = column[Long]("amount")
  }
}