package models.entities

import models.attributes.Attributes
import models.location.{Coordinates, Location, SurfaceLocation}

case class PlanetRegionEntity(galaxyName: String, name: String, attributes: Attributes, coordinates: Coordinates) extends Entity {
  override val entityType: String = Entity.planetRegion
  override val location: Location = SurfaceLocation(attributes.getOrException[Entity](Attributes.parent), coordinates)
  override val signature: BigDecimal = 1
}
