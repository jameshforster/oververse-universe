package helpers

import models.attributes.{Attributes, VariableAttribute}

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
}
