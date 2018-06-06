package models.location

import models.entities.Entity
import play.api.libs.functional.syntax._
import play.api.libs.json._

trait Location {
  val sector: Coordinates
  val system: Coordinates
  val area: Coordinates
  val optPosition: Option[Coordinates]
  val optInsideEntity: Option[Entity]
}

object Location {

  def apply(galactic: Coordinates, system: Coordinates, area: Coordinates, position: Option[Coordinates], insideEntity: Option[Entity]): Location = {
    (position, insideEntity) match {
      case (Some(positionVal), Some(entityVal)) => InternalLocation(positionVal, entityVal)
      case _ => ExternalLocation(galactic, system, area)
    }
  }

  private val writes: Writes[Location] = (o: Location) => Json.obj(
    "sector" -> o.sector,
    "system" -> o.system,
    "area" -> o.area,
    "position" -> o.optPosition,
    "insideEntity" -> o.optInsideEntity
  )

  private val reads: Reads[Location] = (json: JsValue) => (
    (JsPath \ "sector").read[Coordinates] and
      (JsPath \ "system").read[Coordinates] and
      (JsPath \ "area").read[Coordinates] and
      (JsPath \ "position").readNullable[Coordinates] and
      (JsPath \ "insideEntity").readNullable[Entity]
    ) (Location.apply _).reads(json)

  implicit val formats: Format[Location] = Format(reads, writes)
}
