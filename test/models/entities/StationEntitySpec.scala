package models.entities

import helpers.{EntityHelper, UnitSpec}
import models.exceptions.InvalidAttributeException
import models.location.Coordinates

class StationEntitySpec extends UnitSpec {

  "The station entity" should {

    "not throw an exception" when {

      "provided with valid attributes" in {
        StationEntity("randomId", "galaxyName", "planetName", EntityHelper.validStationAttributes, Coordinates(1, 4), Coordinates(3, 6), 50) shouldBe an [Entity]
      }
    }

    "throw an InvalidAttributeException" when {

      EntityHelper.validStationAttributes.attributes.foreach { x =>

        s"missing the ${x._1} attribute" in {
          the[InvalidAttributeException] thrownBy StationEntity(
            "randomId",
            "galaxyName",
            "planetName",
            EntityHelper.validStationAttributes.removeAttribute(x._1),
            Coordinates(1, 4),
            Coordinates(3, 6),
            50) should have message s"Attribute: ${x._1} not found!"
        }
      }
    }
  }
}
