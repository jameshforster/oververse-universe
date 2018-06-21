package models.requests

import models.entities.Entity
import models.location.Coordinates
import play.api.libs.json.{JsObject, JsValue, Json, OFormat}

case class StarQueryRequest(galaxyName: String,
                            by: String,
                            name: Option[String] = None,
                            category: Option[String] = None,
                            galacticCoordinates: Option[Coordinates] = None) {
  def query(): JsObject = {
    JsObject(
      Map(
        "galaxyName" -> Json.toJson(galaxyName),
        "entityType" -> Json.toJson(Entity.star)
      ) ++ {
        by match {
          case "name" if name.isDefined => Map("name" -> Json.toJson(name.get))
          case "category" if category.isDefined => Map("attributes.attributes.category" -> Json.toJson(category.get))
          case "coordinates" if galacticCoordinates.isDefined => Map("location.galactic" -> Json.toJson(galacticCoordinates.get))
          case _ => Map[String, JsValue]()
        }
      }
    )
  }
}

object StarQueryRequest {
  implicit val formats: OFormat[StarQueryRequest] = Json.format[StarQueryRequest]
}
