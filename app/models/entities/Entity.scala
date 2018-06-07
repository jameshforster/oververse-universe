package models.entities

import models.attributes.Attributes
import models.exceptions.UnknownEntityException
import models.location.Location
import play.api.libs.functional.syntax._
import play.api.libs.json._

trait Entity {
  val name: String
  val entityType: String
  val attributes: Attributes
  val location: Location
  val signature: BigDecimal
}

object Entity {

  def apply(nameVal: String, entityTypeVal: String, attributesVal: Attributes, locationVal: Location, signature: BigDecimal): Entity = {
    entityTypeVal match {
      case `star` => StarEntity(nameVal, attributesVal, locationVal.sector, signature)
      case `planet` => PlanetEntity(nameVal, attributesVal, locationVal.sector, locationVal.system, signature)
      case `planetRegion` => PlanetRegionEntity(nameVal, attributesVal, locationVal.area)
      case  unknown => throw new UnknownEntityException(unknown)
    }
  }

  private val writes = new Writes[Entity] {
    override def writes(o: Entity): JsValue = Json.obj(
      "name" -> o.name,
      "entityType" -> o.entityType,
      "attributes" -> o.attributes,
      "location" -> o.location,
      "signature" -> o.signature
    )
  }

  private val reads = new Reads[Entity] {
    override def reads(json: JsValue): JsResult[Entity] = (
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
  val planetRegion = "planetRegion"
}