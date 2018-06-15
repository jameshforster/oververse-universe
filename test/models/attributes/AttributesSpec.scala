package models.attributes

import helpers.UnitSpec
import models.exceptions.InvalidAttributeException
import models.location.Coordinates
import play.api.libs.json.Json

class AttributesSpec extends UnitSpec {

  val testAttributes: Attributes = Attributes(
    Map(
      "intKey" -> Json.toJson(1),
      "stringKey" -> Json.toJson("value"),
      "coordinatesKey" -> Json.toJson(Coordinates(0, 0))
    )
  )

  "Calling .getAttribute" should {

    "return a None" when {

      "looking for an int" which {

        "does not exist" in {
          testAttributes.getAttribute[Int]("fakeKey") shouldBe None
        }

        "is the incorrect type" in {
          testAttributes.getAttribute[Int]("stringKey") shouldBe None
        }
      }

      "looking for a string" which {

        "does not exist" in {
          testAttributes.getAttribute[String]("fakeKey") shouldBe None
        }

        "is the incorrect type" in {
          testAttributes.getAttribute[String]("coordinatesKey") shouldBe None
        }
      }

      "looking for a location" which {

        "does not exist" in {
          testAttributes.getAttribute[Coordinates]("fakeKey") shouldBe None
        }

        "is the incorrect type" in {
          testAttributes.getAttribute[Coordinates]("intKey") shouldBe None
        }
      }
    }

    "return Some value" when {

      "looking for an int" which {

        "exists" in {
          testAttributes.getAttribute[Int]("intKey") shouldBe Some(1)
        }
      }

      "looking for a string" which {

        "exists with a matching type" in {
          testAttributes.getAttribute[String]("stringKey") shouldBe Some("value")
        }
      }

      "looking for a location" which {

        "exists" in {
          testAttributes.getAttribute[Coordinates]("coordinatesKey") shouldBe Some(Coordinates(0, 0))
        }
      }
    }
  }

  "Calling .getOrException" should {

    "return a value" when {

      "getAttribute returns a Some" in {
        testAttributes.getOrException[Coordinates]("coordinatesKey") shouldBe Coordinates(0, 0)
      }
    }

    "throw an InvalidAttributeException" when {

      "getAttribute returns a None" in {
        the[InvalidAttributeException] thrownBy testAttributes.getOrException[Coordinates]("fakeKey") should have message "Attribute: fakeKey not found!"
      }
    }
  }

  "Calling .addOrUpdate" should {

    "append a value to the attributes" when {

      "a non-matching key is provided" in {
        testAttributes.addOrUpdate[Int]("newKey", 2) shouldBe Attributes(
          Map(
            "intKey" -> Json.toJson(1),
            "stringKey" -> Json.toJson("value"),
            "coordinatesKey" -> Json.toJson(Coordinates(0, 0)),
            "newKey" -> Json.toJson(2)
          )
        )
      }
    }

    "update one of the attributes" when {

      "a matching key is provided" which {

        "has the same data type" in {
          testAttributes.addOrUpdate[Int]("intKey", 2) shouldBe Attributes(
            Map(
              "intKey" -> Json.toJson(2),
              "stringKey" -> Json.toJson("value"),
              "coordinatesKey" -> Json.toJson(Coordinates(0, 0))
            )
          )
        }

        "has a different data type" in {
          testAttributes.addOrUpdate[String]("intKey", "newValue") shouldBe Attributes(
            Map(
              "intKey" -> Json.toJson("newValue"),
              "stringKey" -> Json.toJson("value"),
              "coordinatesKey" -> Json.toJson(Coordinates(0, 0))
            )
          )
        }
      }
    }
  }
}