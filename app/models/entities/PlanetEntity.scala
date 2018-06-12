package models.entities
import models.PlanetType
import models.attributes.Attributes
import models.location.Coordinates

case class PlanetEntity(galaxyName: String, name: String, attributes: Attributes, galacticCoordinates: Coordinates, orbitalCoordinates: Coordinates, signature: BigDecimal) extends OrbitalEntity {
  override val entityType: String = Entity.planet

  val size: Int = attributes.getOrException[Int](Attributes.size)
  val atmosphere: Int = attributes.getOrException[Int](Attributes.atmosphere)
  val geology: Int = attributes.getOrException[Int](Attributes.geology)
  val biosphere: Int = attributes.getOrException[Int](Attributes.biosphere)
  val toxicity: Int = attributes.getOrException[Int](Attributes.toxicity)
  val radioactivity: Int = attributes.getOrException[Int](Attributes.radioactivity)
  val minerals: Int = attributes.getOrException[Int](Attributes.minerals)
  val danger: Int = attributes.getOrException[Int](Attributes.danger)
  val breathable: Boolean = attributes.getOrException[Int](Attributes.breathable) > 1
  val water: Int = attributes.getOrException[Int](Attributes.water)
  val temperature: Int = attributes.getOrException[Int](Attributes.temperature)
  val planetType: PlanetType = attributes.getOrException[String](Attributes.planetType)
}
