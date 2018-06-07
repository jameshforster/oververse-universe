package models.entities

import models.location.{Coordinates, ExternalLocation, Location}

trait OrbitalEntity extends Entity {
  val galacticCoordinates: Coordinates
  val orbitalCoordinates: Coordinates

  override lazy val location: Location = ExternalLocation(
    galacticCoordinates,
    orbitalCoordinates,
    Coordinates(0,0))
}
