package models.entities

import models.location.{Coordinates, Location}

trait OrbitalEntity extends Entity {
  val galacticCoordinates: Coordinates
  val orbitalCoordinates: Coordinates

  override lazy val location: Location = Location(
    galacticCoordinates,
    orbitalCoordinates
  )
}
