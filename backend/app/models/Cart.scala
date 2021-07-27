package models

import java.util.Date

import play.api.libs.json.{Json, OFormat}

case class Cart(id : Long, createdTime : String, user : Long, purchased : Boolean)

object Cart {
  implicit val cartFormat : OFormat[Cart] = Json.format[Cart]
}


