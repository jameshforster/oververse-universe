package models.requests

import models.PlanetType
import models.entities.Entity
import models.location.{Coordinates, Location}
import play.api.libs.json.{JsObject, JsValue, Json, OFormat}

case class PlanetQueryRequest(galaxyName: String,
                              by: String,
                              name: Option[String],
                              planetType: Option[PlanetType],
                              galacticCoordinates: Option[Coordinates],
                              systemCoordinates: Option[Coordinates]) {

  def query(): JsObject = {
    JsObject(
      Map(
        "galaxyName" -> Json.toJson(galaxyName),
        "entityType" -> Json.toJson(Entity.planet)
      ) ++ {
        by match {
          case "name" if name.isDefined => Map("name" -> Json.toJson(name.get))
          case "type" if planetType.isDefined => Map("attributes.attributes.planetType" -> Json.toJson(planetType.get))
          case "galacticLocation" if galacticCoordinates.isDefined => Map("location.galactic" -> Json.toJson(galacticCoordinates.get))
          case "location" if galacticCoordinates.isDefined && systemCoordinates.isDefined => Map(
            "location" -> Json.toJson(Location(galacticCoordinates.get, systemCoordinates.get))
          )
          case _ => Map[String, JsValue]()
        }
      }
    )
  }
}

object PlanetQueryRequest {
  implicit val formats: OFormat[PlanetQueryRequest] = Json.format[PlanetQueryRequest]
}
