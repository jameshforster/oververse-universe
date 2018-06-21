package models.entities
import models.attributes.Attributes
import models.location.Coordinates

case class StationEntity(id: String, galaxyName: String, name: String, attributes: Attributes, galacticCoordinates: Coordinates, orbitalCoordinates: Coordinates, signature: BigDecimal) extends OrbitalEntity with ArtificialEntity {
  override val entityType: String = Entity.station

  val size: Int = attributes.getOrException[Int](Attributes.size)
  val atmosphere: Int = attributes.getOrException[Int](Attributes.atmosphere)
  val toxicity: Int = attributes.getOrException[Int](Attributes.toxicity)
  val radioactivity: Int = attributes.getOrException[Int](Attributes.radioactivity)
  val danger: Int = attributes.getOrException[Int](Attributes.danger)
  val breathable: Boolean = attributes.getOrException[Int](Attributes.breathable) > 1
  val temperature: Int = attributes.getOrException[Int](Attributes.temperature)

  val children: Option[List[String]] = attributes.getAttribute[List[String]](Attributes.children)
}
