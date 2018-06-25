package models.entities

import models.attributes.Attributes
import models.exceptions.UnknownEntityException
import models.location.Location
import play.api.libs.functional.syntax._
import play.api.libs.json._

trait Entity {
  val id: String
  val galaxyName: String
  val name: String
  val entityType: String
  val attributes: Attributes
  val location: Location
  val signature: BigDecimal
}

object Entity {

  def apply(idVal: String, galaxyNameVal: String, nameVal: String, entityTypeVal: String, attributesVal: Attributes, locationVal: Location, signature: BigDecimal): Entity = {
    entityTypeVal match {
      case `star` => StarEntity(idVal, galaxyNameVal, nameVal, attributesVal, locationVal.galactic, signature)
      case `planet` => PlanetEntity(idVal, galaxyNameVal, nameVal, attributesVal, locationVal.galactic, locationVal.system, signature)
      case `station` => StationEntity(idVal, galaxyNameVal, nameVal, attributesVal, locationVal.galactic, locationVal.system, signature)
      case unknown => throw new UnknownEntityException(unknown)
    }
  }

  private val writes = new Writes[Entity] {
    override def writes(o: Entity): JsValue = Json.obj(
      "entityId" -> o.id,
      "galaxyName" -> o.galaxyName,
      "name" -> o.name,
      "entityType" -> o.entityType,
      "attributes" -> o.attributes,
      "location" -> o.location,
      "signature" -> o.signature
    )
  }

  private val reads = new Reads[Entity] {
    override def reads(json: JsValue): JsResult[Entity] = (
      (JsPath \ "entityId").read[String] and
      (JsPath \ "galaxyName").read[String] and
        (JsPath \ "name").read[String] and
        (JsPath \ "entityType").read[String] and
        (JsPath \ "attributes").read[Attributes] and
        (JsPath \ "location").read[Location] and
        (JsPath \ "signature").read[BigDecimal]
      ) (apply _).reads(json)
  }

  implicit val formats: Format[Entity] = Format(reads, writes)

  val star = "star"
  val planet = "planet"
  val station = "station"
}