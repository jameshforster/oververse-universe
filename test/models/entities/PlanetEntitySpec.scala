package models.entities

import helpers.{EntityHelper, UnitSpec}
import models.exceptions.InvalidAttributeException
import models.location.Coordinates

class PlanetEntitySpec extends UnitSpec {

  "The planet entity" should {

    "not throw an exception" when {

      "provided with valid attributes" in {
        PlanetEntity("galaxyName", "planetName", EntityHelper.validPlanetAttributes, Coordinates(1, 4), Coordinates(3, 6), 50) shouldBe an [Entity]
      }
    }

    "throw an InvalidAttributeException" when {

      EntityHelper.validPlanetAttributes.attributes.foreach { x =>

        s"missing the ${x._1} attribute" in {
          the[InvalidAttributeException] thrownBy PlanetEntity(
            "galaxyName",
            "planetName",
            EntityHelper.validPlanetAttributes.removeAttribute(x._1),
            Coordinates(1, 4),
            Coordinates(3, 6),
            50) should have message s"Attribute: ${x._1} not found!"
        }
      }
    }
  }
}
