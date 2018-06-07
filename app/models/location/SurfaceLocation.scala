package models.location

import models.entities.Entity

case class SurfaceLocation(parent: Entity, area: Coordinates) extends Location {
  override val sector: Coordinates = parent.location.sector
  override val system: Coordinates = parent.location.system
  override val optPosition: Option[Coordinates] = None
  override val optInsideEntity: Option[Entity] = None
}
