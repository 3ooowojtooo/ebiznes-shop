package repository

import controllers.dto.CategoryDto
import javax.inject.{Inject, Singleton}
import models.Category
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  val category = TableQuery[CategoryTable]

  def create(name: String): Future[Category] = db.run {
    (category.map(c => c.name)
      returning category.map(_.id)
      into ((name, id) => Category(id, name))
      ) += name
  }

  def list(): Future[List[CategoryDto]] = db.run {
    category.result.map(_.toStream
    .map(CategoryDto.apply)
    .toList)
  }

  def getById(id: Long): Future[CategoryDto] = db.run {
    category.filter(_.id === id).result.head.map(CategoryDto.apply)
  }

  def getByIdOption(id: Long): Future[Option[CategoryDto]] = db.run {
    category.filter(_.id === id).result.headOption.map {
      case Some(value) => Some(CategoryDto(value))
      case None => None
    }
  }

  def delete(id: Long): Future[Unit] = db.run(category.filter(_.id === id).delete.map(_ => ()))

  def update(id: Long, name: String): Future[Unit] = {
    val newCategory = new Category(id, name)
    db.run(category.filter(_.id === id).update(newCategory).map(_ => ()))
  }

  class CategoryTable(tag: Tag) extends Table[Category](tag, "category") {
    def * = (id, name) <> ((Category.apply _).tupled, Category.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")
  }
}