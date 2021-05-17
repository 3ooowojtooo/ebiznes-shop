package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class UserTable(tag: Tag) extends Table[User](tag, "user") {

    def id = column[Long]("id")

    def name = column[String]("name")

    def age = column[Int]("age")

    override def * = (id, name, age) <> ((User.apply _).tupled, User.unapply)
  }

  private val user = TableQuery[UserTable]

  def create(name: String, age: Int): Future[User] = db.run {
    (user.map(u => (u.name, u.age))
      returning user.map(_.id)
      into { case ((name, age), id) => User(id, name, age) }
      ) += (name, age)
  }

  def list : Future[Seq[User]] = db.run {
    user.result
  }

  def getById(id : Long) : Future[User] = db.run {
    user.filter(_.id === id).result.head
  }

  def getByIdOption(id : Long) : Future[Option[User]] = db.run {
    user.filter(_.id === id).result.headOption
  }

  def delete(id : Long) : Future[Unit] = db.run {
    user.filter(_.id === id).delete.map(_ => ())
  }
}