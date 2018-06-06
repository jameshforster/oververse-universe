package models.entities

import models.location.{Coordinates, ExternalLocation, Location}

trait OrbitalEntity extends Entity {
  val systemCoordinates: Coordinates
  val orbitalCoordinates: Coordinates

  override lazy val location: Location = ExternalLocation(
    systemCoordinates,
    orbitalCoordinates,
    Coordinates(0,0))
}
