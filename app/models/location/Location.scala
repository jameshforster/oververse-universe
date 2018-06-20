package models.location

import play.api.libs.json.{Json, OFormat}

case class Location(galactic: Coordinates,
                    system: Coordinates,
                    region: Option[Coordinates] = None,
                    area: Option[Coordinates] = None,
                    position: Option[Coordinates] = None,
                    layer: Option[Int] = None
                   )

object Location {
  implicit val formats: OFormat[Location] = Json.format[Location]
}