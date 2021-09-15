package repository

import controllers.dto.ProductDto
import javax.inject.{Inject, Singleton}
import models.Product
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider, val categoryRepository: CategoryRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val product = TableQuery[ProductTable]

  import categoryRepository.CategoryTable

  private val cat = TableQuery[CategoryTable]

  def create(name: String, description: String, category: Long, price: Double): Future[Product] = db.run {
    (product.map(p => (p.name, p.description, p.category, p.price))
      returning product.map(_.id)
      into { case ((name, description, category, price), id) => Product(id, name, description, category, price) }
      ) += (name, description, category, price)
  }

  def list: Future[List[ProductDto]] = db.run {
    val joinQuery = for {
      (p, c) <- product join cat on (_.category === _.id)
    } yield (p, c)
    joinQuery.result
      .map(_.toStream
        .map(data => ProductDto(data._1, data._2))
        .toList)
  }

  def getById(id: Long): Future[ProductDto] = db.run {
    val joinQuery = for {
      (p, c) <- product join cat on (_.category === _.id)
    } yield (p, c)
    joinQuery.filter(_._1.id === id).result.head
        .map(data => ProductDto(data._1, data._2))
  }

  def getByIdOption(id: Long): Future[Option[ProductDto]] = db.run {
    val joinQuery = for {
      (p, c) <- product join cat on (_.category === _.id)
    } yield (p, c)
    joinQuery.filter(_._1.id === id).result.headOption
      .map {
        case Some(value) => Some(ProductDto(value._1, value._2))
        case None => None
      }
  }

  def delete(id: Long): Future[Unit] = db.run(product.filter(_.id === id).delete).map(_ => ())

  def update(id: Long, name: String, description: String, category: Long, price: Double): Future[Unit] = {
    val productToUpdate: Product = Product(id, name, description, category, price)
    db.run(product.filter(_.id === id).update(productToUpdate)).map(_ => ())
  }

  class ProductTable(tag: Tag) extends Table[Product](tag, "product") {
    def fkCategory = foreignKey("cat_fk", category, cat)(_.id)

    def category = column[Long]("category")

    def * = (id, name, description, category, price) <> ((Product.apply _).tupled, Product.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def description = column[String]("description")

    def price = column[Double]("price")
  }

}