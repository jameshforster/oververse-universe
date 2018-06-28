package helpers

import models.StarSystem
import models.attributes.{Attributes, VariableAttribute}
import models.entities.{PlanetEntity, StarEntity, StationEntity}
import models.location.Coordinates

object EntityHelper {

  val validPlanetAttributes: Attributes = Attributes.emptyAttributes
    .addOrUpdate(Attributes.size, 5)
    .addOrUpdate(Attributes.atmosphere, 1)
    .addOrUpdate(Attributes.geology, 1)
    .addOrUpdate(Attributes.biosphere, 1)
    .addOrUpdate(Attributes.toxicity, 1)
    .addOrUpdate(Attributes.radioactivity, 1)
    .addOrUpdate(Attributes.minerals, 1)
    .addOrUpdate(Attributes.danger, 1)
    .addOrUpdate(Attributes.breathable, 1)
    .addOrUpdate(Attributes.water, 1)
    .addOrUpdate(Attributes.temperature, 1)
    .addOrUpdate(Attributes.planetType, "Barren")

  val validStationAttributes: Attributes = Attributes.emptyAttributes
    .addOrUpdate(Attributes.size, 5)
    .addOrUpdate(Attributes.atmosphere, 1)
    .addOrUpdate(Attributes.toxicity, 1)
    .addOrUpdate(Attributes.radioactivity, 1)
    .addOrUpdate(Attributes.danger, 1)
    .addOrUpdate(Attributes.breathable, 1)
    .addOrUpdate(Attributes.temperature, 1)
    .addOrUpdate(Attributes.armour, VariableAttribute(1, 1))
    .addOrUpdate(Attributes.shields, 1)
    .addOrUpdate(Attributes.hp, VariableAttribute(1, 1))
    .addOrUpdate(Attributes.pointDefenses, 1)
    .addOrUpdate(Attributes.weapons, 1)


  def validStarAttributes(size: Int, colour: String): Attributes = Attributes.emptyAttributes
    .addOrUpdate(Attributes.size, size)
    .addOrUpdate(Attributes.colour, colour)

  val testStar = StarEntity("fakeId", "galaxyName", "name", EntityHelper.validStarAttributes(3, "Red"), Coordinates(1, 2), 300)
  val testPlanet = PlanetEntity("fakeId", "galaxyName", "name", EntityHelper.validPlanetAttributes, Coordinates(1, 2), Coordinates(3, 4), 10)
  val testStation = StationEntity("fakeId", "galaxyName", "name", EntityHelper.validStationAttributes, Coordinates(1, 2), Coordinates(3, 4), 10)
  val testSystem = StarSystem(testStar, Seq(testPlanet, testPlanet), Seq(testStation), Seq())
}
