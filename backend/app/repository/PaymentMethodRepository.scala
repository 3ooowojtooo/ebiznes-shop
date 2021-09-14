package repository

import controllers.dto.PaymentMethodDto
import models.PaymentMethod
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Inject
class PaymentMethodRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider, val userRepository: UserRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val paymentMethodTable = TableQuery[PaymentMethodTable]

  import userRepository.UserTable

  private val userTable = TableQuery[UserTable]

  def create(user: Long, name: String): Future[PaymentMethod] = db.run {
    (paymentMethodTable.map(c => (c.user, c.name))
      returning paymentMethodTable.map(_.id)
      into { case ((user, name), id) => PaymentMethod(id, user, name) }
      ) += (user, name)
  }

  def list: Future[List[PaymentMethodDto]] = db.run {
    val joinQuery = for {
      (p, u) <- paymentMethodTable join userTable on (_.user === _.id)
    } yield (p, u)
    joinQuery.result
      .map(_.toStream
        .map(data => PaymentMethodDto(data._1, data._2))
        .toList)
  }

  def getById(id: Long): Future[PaymentMethodDto] = db.run {
    val joinQuery = for {
      (p, u) <- paymentMethodTable join userTable on (_.user === _.id)
    } yield (p, u)
    joinQuery.filter(_._1.id === id).result.head
      .map(data => PaymentMethodDto(data._1, data._2))
  }

  def getByIdOption(id: Long): Future[Option[PaymentMethodDto]] = db.run {
    val joinQuery = for {
      (p, u) <- paymentMethodTable join userTable on (_.user === _.id)
    } yield (p, u)
    joinQuery.filter(_._1.id === id).result.headOption
      .map {
        case Some(value) => Some(PaymentMethodDto(value._1, value._2))
        case None => None
      }
  }

  def delete(id: Long): Future[Unit] = db.run(paymentMethodTable.filter(_.id === id).delete.map(_ => ()))

  def delete(id: Long, userId: Long): Future[Unit] = db.run {
    paymentMethodTable
      .filter(_.id === id)
      .filter(_.user === userId)
      .delete
      .map(_ => ())
  }

  def update(id: Long, user: Long, name: String): Future[Unit] = {
    val newPaymentMethod = PaymentMethod(id, user, name)
    db.run(paymentMethodTable.filter(_.id === id).update(newPaymentMethod).map(_ => ()))
  }

  def getByUserAndName(user : Long, name : String) : Future[Option[PaymentMethod]] = db.run {
    paymentMethodTable
      .filter(_.user === user)
      .filter(_.name === name)
      .result
      .headOption
  }

  def getUserPaymentMethods(userId: Long): Future[Seq[PaymentMethod]] = db.run {
    paymentMethodTable
      .filter(_.user === userId)
      .result
  }

  class PaymentMethodTable(tag: Tag) extends Table[PaymentMethod](tag, "payment_method") {
    def fkUser = foreignKey("fk_user", user, userTable)(_.id)

    def user = column[Long]("user")

    def * = (id, user, name) <> ((PaymentMethod.apply _).tupled, PaymentMethod.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")
  }
}
