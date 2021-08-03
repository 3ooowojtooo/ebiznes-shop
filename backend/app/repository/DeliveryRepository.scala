package repository

import javax.inject.Inject
import models.Delivery
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Inject
class DeliveryRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider, val cartRepository: CartRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._
  private val deliveryTable = TableQuery[DeliveryTable]

  import cartRepository.CartTable
  private val cartTable = TableQuery[CartTable]

  def create(cart: Long, deliveryTimestamp: String, delivered: Boolean): Future[Delivery] = db.run {
    (deliveryTable.map(c => (c.cart, c.deliveryTimestamp, c.delivered))
      returning deliveryTable.map(_.id)
      into { case ((cart, deliveryTimestamp, delivered), id) => Delivery(id, cart, deliveryTimestamp, delivered) }
      ) += (cart, deliveryTimestamp, delivered)
  }

  def list: Future[Seq[Delivery]] = db.run {
    deliveryTable.result
  }

  def getById(id: Long): Future[Delivery] = db.run {
    deliveryTable.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Delivery]] = db.run {
    deliveryTable.filter(_.id === id).result.headOption
  }

  def delete(id: Long): Future[Unit] = db.run(deliveryTable.filter(_.id === id).delete.map(_ => ()))

  def update(id: Long, cart: Long, deliveryTimestamp: String, delivered: Boolean): Future[Unit] = {
    val newDelivery = Delivery(id, cart, deliveryTimestamp, delivered)
    db.run(deliveryTable.filter(_.id === id).update(newDelivery).map(_ => ()))
  }

  class DeliveryTable(tag: Tag) extends Table[Delivery](tag, "delivery") {
    def fk_cart = foreignKey("fk_cart", cart, cartTable)(_.id)

    def cart = column[Long]("cart")

    def * = (id, cart, deliveryTimestamp, delivered) <> ((Delivery.apply _).tupled, Delivery.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def deliveryTimestamp = column[String]("delivery_timestamp")

    def delivered = column[Boolean]("delivered")
  }
}
