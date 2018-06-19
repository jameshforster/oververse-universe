package helpers

import models.attributes.Attributes

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
}
