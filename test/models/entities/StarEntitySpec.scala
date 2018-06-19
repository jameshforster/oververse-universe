package models.entities

import helpers.{EntityHelper, UnitSpec}
import models.exceptions.InvalidAttributeException
import models.location.Coordinates

class StarEntitySpec extends UnitSpec {

  "The star entity" should {

    "throw an InvalidAttributeException" when {
      val validAttributes = EntityHelper.validStarAttributes(5, "Red")

      validAttributes.attributes.foreach { x =>

        s"missing the ${x._1} attribute" in {
          the[InvalidAttributeException] thrownBy StarEntity(
            "galaxyName",
            "starName",
            validAttributes.removeAttribute(x._1),
            Coordinates(1, 4),
            500) should have message s"Attribute: ${x._1} not found!"
        }
      }
    }

    "have a valid special category" when {

      "provided with a colour of black" in {
        StarEntity("galaxyName",
          "starName",
          EntityHelper.validStarAttributes(5, "Black"),
          Coordinates(1, 4),
          50).category shouldBe "Black Hole"
      }

      "provided with a colour of neutron" in {
        StarEntity("galaxyName",
          "starName",
          EntityHelper.validStarAttributes(7, "Neutron"),
          Coordinates(1, 4),
          50).category shouldBe "Neutron Star"
      }
    }

    "have a valid dwarf category" when {

      "provided with a size of 1" in {
        StarEntity("galaxyName",
          "starName",
          EntityHelper.validStarAttributes(1, "Brown"),
          Coordinates(1, 4),
          50).category shouldBe "Brown Dwarf"
      }

      "provided with a size of 2" in {
        StarEntity("galaxyName",
          "starName",
          EntityHelper.validStarAttributes(2, "White"),
          Coordinates(1, 4),
          50).category shouldBe "White Dwarf"
      }
    }

    "have a valid main star category" when {

      "provided with a size of 3" in {
        StarEntity("galaxyName",
          "starName",
          EntityHelper.validStarAttributes(3, "Yellow"),
          Coordinates(1, 4),
          50).category shouldBe "Yellow Star"
      }

      "provided with a size of 6" in {
        StarEntity("galaxyName",
          "starName",
          EntityHelper.validStarAttributes(6, "Blue"),
          Coordinates(1, 4),
          50).category shouldBe "Blue Star"
      }
    }

    "have a valid giant category" when {

      "provided with a size of 7" in {
        StarEntity("galaxyName",
          "starName",
          EntityHelper.validStarAttributes(7, "Red"),
          Coordinates(1, 4),
          50).category shouldBe "Red Giant"
      }

      "provided with a size of 9" in {
        StarEntity("galaxyName",
          "starName",
          EntityHelper.validStarAttributes(9, "Blue"),
          Coordinates(1, 4),
          50).category shouldBe "Blue Giant"
      }
    }

    "have a valid supergiant category" when {

      "provided with a size of 10" in {
        StarEntity("galaxyName",
          "starName",
          EntityHelper.validStarAttributes(10, "Blue"),
          Coordinates(1, 4),
          50).category shouldBe "Blue Supergiant"
      }
    }
  }

}
