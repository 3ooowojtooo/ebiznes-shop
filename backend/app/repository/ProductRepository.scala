package repository

import javax.inject.{Inject, Singleton}
import models.Product
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductRepository @Inject() (val dbConfigProvider: DatabaseConfigProvider, val categoryRepository: CategoryRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ProductTable(tag: Tag) extends Table[Product](tag, "product") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def description = column[String]("description")
    def category = column[Long]("category")
    def price = column[Double]("price")
    def category_fk = foreignKey("cat_fk",category, cat)(_.id)
    def * = (id, name, description, category, price) <> ((Product.apply _).tupled, Product.unapply)
  }

  import categoryRepository.CategoryTable

  private val product = TableQuery[ProductTable]
  private val cat = TableQuery[CategoryTable]

  def create(name: String, description: String, category: Long, price : Double): Future[Product] = db.run {
    (product.map(p => (p.name, p.description,p.category,p.price))
      returning product.map(_.id)
      into {case ((name,description,category, price),id) => Product(id,name, description,category, price)}
      ) += (name, description,category, price)
  }

  def list: Future[Seq[Product]] = db.run {
    product.result
  }

  def getByCategory(category_id: Long): Future[Seq[Product]] = db.run {
    product.filter(_.category === category_id).result
  }

  def getById(id: Long): Future[Product] = db.run {
    product.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Product]] = db.run {
    product.filter(_.id === id).result.headOption
  }

  def getByCategories(category_ids: List[Long]): Future[Seq[Product]] = db.run {
    product.filter(_.category inSet category_ids).result
  }

  def delete(id: Long): Future[Unit] = db.run(product.filter(_.id === id).delete).map(_ => ())

  def update(id: Long, name: String, description: String, category: Long, price : Double): Future[Unit] = {
    val productToUpdate: Product = Product(id, name, description, category, price)
    db.run(product.filter(_.id === id).update(productToUpdate)).map(_ => ())
  }

}