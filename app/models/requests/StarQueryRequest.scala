package models.requests

import models.Galaxy
import models.entities.StarEntity
import models.location.Coordinates
import play.api.libs.json.{Json, OFormat}

case class StarQueryRequest(galaxyName: String,
                            by: String,
                            name: Option[String],
                            category: Option[String],
                            galacticCoordinates: Option[Coordinates]) {
  def query(galaxy: Galaxy): Seq[StarEntity] = {
    val stars = galaxy.starSystems.map(_.stellarObject.asInstanceOf[StarEntity])

    by match {
      case "name" if name.isDefined => stars.filter(_.name == name.get)
      case "category" if category.isDefined => stars.filter(_.category == category.get)
      case "coordinates" if galacticCoordinates.isDefined => stars.filter(_.location.sector == galacticCoordinates.get)
      case _ => stars
    }
  }
}

object StarQueryRequest {
  implicit val formats: OFormat[StarQueryRequest] = Json.format[StarQueryRequest]
}
