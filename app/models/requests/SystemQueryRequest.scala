package models.requests

import models.entities.StarEntity
import models.location.Coordinates
import models.{Galaxy, StarSystem}
import play.api.libs.json.{Json, OFormat}

case class SystemQueryRequest(galaxyName: String,
                              by: String,
                              name: Option[String],
                              category: Option[String],
                              galacticCoordinates: Option[Coordinates]) extends {
  def query(galaxy: Galaxy): Seq[StarSystem] = {
    val systems = galaxy.starSystems

    by match {
      case "name" if name.isDefined => systems.filter(_.stellarObject.name == name.get)
      case "category" if category.isDefined => systems.filter(_.stellarObject.asInstanceOf[StarEntity].category == category.get)
      case "coordinates" if galacticCoordinates.isDefined => systems.filter(_.stellarObject.location.sector == galacticCoordinates.get)
      case _ => systems
    }
  }
}

object SystemQueryRequest {
  implicit val formats: OFormat[SystemQueryRequest] = Json.format[SystemQueryRequest]
}
