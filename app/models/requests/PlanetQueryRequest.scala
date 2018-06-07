package models.requests

import models.entities.PlanetEntity
import models.location.Coordinates
import models.{Galaxy, PlanetType}
import play.api.libs.json.{Json, OFormat}

case class PlanetQueryRequest(galaxyName: String,
                              by: String,
                              name: Option[String],
                              planetType: Option[PlanetType],
                              galacticCoordinates: Option[Coordinates],
                              systemCoordinates: Option[Coordinates]) {
  def query(galaxy: Galaxy): Seq[PlanetEntity] = {
    val planets = galaxy.starSystems.flatMap(_.majorOrbitals.flatMap {
      case e: PlanetEntity => Some(e)
      case _ => None
    })

    by match {
      case "name" if name.isDefined => planets.filter(_.name == name.get)
      case "type" if planetType.isDefined => planets.filter(_.planetType == planetType.get)
      case "galacticLocation" if galacticCoordinates.isDefined => planets.filter(_.galacticCoordinates == galacticCoordinates.get)
      case "coordinates" if galacticCoordinates.isDefined && systemCoordinates.isDefined => planets.filter{
        planet => planet.galacticCoordinates == galacticCoordinates.get && planet.orbitalCoordinates == systemCoordinates.get
      }
      case _ => planets
    }
  }
}

object PlanetQueryRequest {
  implicit val formats: OFormat[PlanetQueryRequest] = Json.format[PlanetQueryRequest]
}
