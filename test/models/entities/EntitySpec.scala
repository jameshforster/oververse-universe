package models.entities

import helpers.{EntityHelper, UnitSpec}
import models.exceptions.UnknownEntityException
import models.location.{Coordinates, Location}
import org.scalatest.enablers.Messaging
import play.api.libs.json.Json

class EntitySpec extends UnitSpec {

  "An entity" should {

    val testEntity = StarEntity("id", "galaxyName", "starName", EntityHelper.validStarAttributes(3, "Red"), Coordinates(1, 2), 300)
    val attributesJson = Json.toJson(EntityHelper.validStarAttributes(3, "Red"))
    val locationJson = Json.toJson(Location(Coordinates(1, 2), Coordinates(0, 0)))
    val testJson = Json.parse(
      s"""
         |{
         | "entityId" : "id",
         | "galaxyName" : "galaxyName",
         | "name" : "starName",
         | "entityType" : "${Entity.star}",
         | "attributes" : ${Json.prettyPrint(attributesJson)},
         | "location" : ${Json.prettyPrint(locationJson)},
         | "signature" : 300
         |}
        """.stripMargin)

    "correctly write to json" in {

      Json.toJson(testEntity) shouldBe testJson
    }

    "correctly read from json" in {

      Json.fromJson[Entity](testJson).get shouldBe testEntity
    }
  }

  "The Entity apply method" should {

    "create the correct entity" when {

      "provided with a type of planet" in {
        Entity.apply("id", "galaxyName", "entityName", Entity.planet, EntityHelper.validPlanetAttributes, Location(Coordinates(1, 2), Coordinates(3, 4)), 10) shouldBe {
          PlanetEntity("id", "galaxyName", "entityName", EntityHelper.validPlanetAttributes, Coordinates(1, 2), Coordinates(3, 4), 10)
        }
      }

      "provided with a type of star" in {
        Entity.apply("id", "galaxyName", "entityName", Entity.star, EntityHelper.validStarAttributes(1, "Red"), Location(Coordinates(1, 2), Coordinates(3, 4)), 10) shouldBe {
          StarEntity("id", "galaxyName", "entityName", EntityHelper.validStarAttributes(1, "Red"), Coordinates(1, 2), 10)
        }
      }

      "provided with a type of station" in {
        Entity.apply("id", "galaxyName", "entityName", Entity.station, EntityHelper.validStationAttributes, Location(Coordinates(1, 2), Coordinates(3, 4)), 10) shouldBe {
          StationEntity("id", "galaxyName", "entityName", EntityHelper.validStationAttributes, Coordinates(1, 2), Coordinates(3, 4), 10)
        }
      }
    }

    "throw an UnknownEntityException" when {

      "provided with a non-matching type" in {
        the[UnknownEntityException] thrownBy {
          Entity.apply("id", "galaxyName", "entityName", "fakeEntity", EntityHelper.validStationAttributes, Location(Coordinates(1, 2), Coordinates(3, 4)), 10)
        } should have message "fakeEntity is not a valid entity type!"
      }
    }
  }
}
