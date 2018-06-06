package models.location

import models.entities.Entity

case class ExternalLocation(sector: Coordinates, system: Coordinates, area: Coordinates) extends Location {
  override val optPosition: Option[Coordinates] = None
  override val optInsideEntity: Option[Entity] = None
}



