package models.entities
import models.attributes.Attributes
import models.location.Coordinates

case class StationEntity(galaxyName: String, name: String, attributes: Attributes, galacticCoordinates: Coordinates, orbitalCoordinates: Coordinates, signature: BigDecimal) extends OrbitalEntity {
  override val entityType: String = Entity.station
}
