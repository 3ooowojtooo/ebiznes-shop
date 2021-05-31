package models

import play.api.libs.json.{Json, OFormat}

case class Stock(id : Long, product : Long, amount : Long)

object Stock {
  implicit val stockFormat : OFormat[Stock] = Json.format[Stock]
}
