package models.requests

import models.PlanetType
import models.entities.Entity
import models.exceptions.InvalidQueryException
import models.location.{Coordinates, Location}
import play.api.libs.json.{JsObject, JsValue, Json, OFormat}

case class PlanetQueryRequest(galaxyName: String,
                              select: String,
                              name: Option[String] = None,
                              planetType: Option[PlanetType] = None,
                              galacticCoordinates: Option[Coordinates] = None,
                              systemCoordinates: Option[Coordinates] = None) {

  def query(): JsObject = {
    JsObject(
      Map(
        "galaxyName" -> Json.toJson(galaxyName),
        "entityType" -> Json.toJson(Entity.planet)
      ) ++ {
        select match {
          case "name" if name.isDefined => Map("name" -> Json.toJson(name.get))
          case "type" if planetType.isDefined => Map("attributes.attributes.planetType" -> Json.toJson(planetType.get))
          case "galacticLocation" if galacticCoordinates.isDefined => Map("location.galactic" -> Json.toJson(galacticCoordinates.get))
          case "location" if galacticCoordinates.isDefined && systemCoordinates.isDefined => Map(
            "location" -> Json.toJson(Location(galacticCoordinates.get, systemCoordinates.get))
          )
          case "all" => Map[String, JsValue]()
          case _ => throw new InvalidQueryException(select)
        }
      }
    )
  }
}

object PlanetQueryRequest {
  implicit val formats: OFormat[PlanetQueryRequest] = Json.format[PlanetQueryRequest]
}
