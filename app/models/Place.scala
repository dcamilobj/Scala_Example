package models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._


case class Place(id: Int, name: String,  description: Option[String])

object Place {

  /*Writes converters are used to convert from some type to a JsValue.*/
  implicit val placeWrite : Writes[Place] = Json.writes[Place]

  /*Reads converters are used to convert from a JsValue to another type*/

  /*If the JSON maps directly to a class, we provide a handy macro
  so that you don’t have to write the Reads[T], Writes[T], or Format[T] manually.*/
  implicit val placeReader : Reads[Place] = Json.reads[Place]

  /*
  implicit val placeReader : Reads[Place] = (
    (__ \ "id").read[Int] and
    (__ \ "name").read[String] and
    (__ \ "description").readNullable[String]
  )(Place.apply _) */

/*
  implicit val placeRead : Reads[Place] = (
    (__ \ "id").read[Int](min(0) keepAnd max(99)) and
    (__ \ "name").read[String] and
    (__ \ "description").readNullable[String](minLength(3))
    )(Place.apply _)*/

}
