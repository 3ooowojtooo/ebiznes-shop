package repository

import javax.inject.Inject
import models.{Cart, PaymentMethod}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Inject
class PaymentMethodRepository @Inject() (val dbConfigProvider: DatabaseConfigProvider, val userRepository: UserRepository)(implicit ec: ExecutionContext){
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class PaymentMethodTable(tag : Tag) extends Table[PaymentMethod](tag, "payment_method") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def user = column[Long]("user")
    def name = column[String]("name")
    def fk_user = foreignKey("fk_user", user, userTable)(_.id)
    def * = (id, user, name) <> ((PaymentMethod.apply _).tupled, PaymentMethod.unapply)
  }

  import userRepository.UserTable

  private val paymentMethodTable = TableQuery[PaymentMethodTable]
  private val userTable = TableQuery[UserTable]

  def create(user : Long, name : String): Future[PaymentMethod] = db.run {
    (paymentMethodTable.map(c => (c.user, c.name))
      returning paymentMethodTable.map(_.id)
      into {case ((user, name),id) => PaymentMethod(id, user, name)}
      ) += (user, name)
  }

  def list: Future[Seq[PaymentMethod]] = db.run {
    paymentMethodTable.result
  }

  def getById(id : Long) : Future[PaymentMethod] = db.run {
    paymentMethodTable.filter(_.id === id).result.head
  }

  def getByIdOption(id : Long) : Future[Option[PaymentMethod]] = db.run {
    paymentMethodTable.filter(_.id === id).result.headOption
  }

  def delete(id : Long) : Future[Unit] = db.run(paymentMethodTable.filter(_.id === id).delete.map(_ => ()))

  def update(id : Long, user : Long, name : String) : Future[Unit] = {
    val newPaymentMethod = PaymentMethod(id, user, name)
    db.run(paymentMethodTable.filter(_.id === id).update(newPaymentMethod).map(_ => ()))
  }
}
