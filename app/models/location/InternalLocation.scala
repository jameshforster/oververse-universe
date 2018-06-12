package models.location

import models.entities.Entity

case class InternalLocation(position: Coordinates, insideEntity: Entity) extends Location {
  override val galactic: Coordinates = insideEntity.location.galactic
  override val system: Coordinates = insideEntity.location.system
  override val area: Coordinates = insideEntity.location.area
  override val optPosition: Option[Coordinates] = Some(position)
  override val optInsideEntity: Option[Entity] = Some(insideEntity)
}
