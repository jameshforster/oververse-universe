package models.attributes

import models.exceptions.InvalidAttributeException
import play.api.libs.json._

case class Attributes(attributes: Map[String, JsValue]) {

  def getAttribute[A](key: String)(implicit reads: Reads[A]): Option[A] = {
    attributes.get(key).flatMap(x => Json.fromJson(x).asOpt)
  }

  def getOrException[A](key: String)(implicit reads: Reads[A]): A = {
    getAttribute[A](key).getOrElse(throw new InvalidAttributeException(key))
  }

  def addOrUpdate[A](key: String, value: A)(implicit writes: Writes[A]): Attributes = {
    Attributes(attributes + (key -> Json.toJson(value)))
  }

  def removeAttribute(key: String): Attributes = {
    Attributes(attributes - key)
  }
}

object Attributes {
  implicit val formats: OFormat[Attributes] = Json.format[Attributes]
  val emptyAttributes = Attributes(Map())

  val size = "size"
  val colour = "colour"
  val geology = "geology"
  val atmosphere = "atmosphere"
  val biosphere = "biosphere"
  val toxicity = "toxicity"
  val radioactivity = "radioactivity"
  val minerals = "minerals"
  val danger = "danger"
  val breathable = "breathable"
  val water = "water"
  val temperature = "temperature"
  val parent = "parent"
  val planetType = "planetType"
}
