package models.requests

import helpers.UnitSpec
import models.entities.Entity
import models.exceptions.InvalidQueryException
import models.location.{Coordinates, Location}
import play.api.libs.json.{JsObject, Json}

class PlanetQueryRequestSpec extends UnitSpec {

  "The PlanetQueryRequest query method" should {

    val fullQuery: PlanetQueryRequest = PlanetQueryRequest("galaxyName", "all", Some("name"), Some("Barren"), Some(Coordinates(1, 2)), Some(Coordinates(3, 4)))

    "return the correct Json query" when {

      "querying by name with name data" in {
        PlanetQueryRequest("galaxyName", "name", name = Some("name")).query() shouldBe JsObject(
          Map(
            "galaxyName" -> Json.toJson("galaxyName"),
            "entityType" -> Json.toJson(Entity.planet),
            "name" -> Json.toJson("name")
          )
        )
      }

      "querying by type with type data" in {
        PlanetQueryRequest("galaxyName", "type", planetType = Some("Barren")).query() shouldBe JsObject(
          Map(
            "galaxyName" -> Json.toJson("galaxyName"),
            "entityType" -> Json.toJson(Entity.planet),
            "attributes.attributes.planetType" -> Json.toJson("Barren")
          )
        )
      }

      "querying by location with only galactic coordinates data" in {
        PlanetQueryRequest("galaxyName", "location", galacticCoordinates = Some(Coordinates(1, 2))).query() shouldBe JsObject(
          Map(
            "galaxyName" -> Json.toJson("galaxyName"),
            "entityType" -> Json.toJson(Entity.planet),
            "location.galactic" -> Json.toJson(Coordinates(1, 2))
          )
        )
      }

      "querying by location with full location data" in {
        PlanetQueryRequest("galaxyName", "location", galacticCoordinates = Some(Coordinates(1, 2)), systemCoordinates = Some(Coordinates(3, 4))).query() shouldBe JsObject(
          Map(
            "galaxyName" -> Json.toJson("galaxyName"),
            "entityType" -> Json.toJson(Entity.planet),
            "location" -> Json.toJson(Location(Coordinates(1, 2), Coordinates(3, 4)))
          )
        )
      }

      "querying by all with no data" in {
        PlanetQueryRequest("galaxyName", "all").query() shouldBe JsObject(
          Map(
            "galaxyName" -> Json.toJson("galaxyName"),
            "entityType" -> Json.toJson(Entity.planet)
          )
        )
      }

      "querying by all with all data" in {
        PlanetQueryRequest("galaxyName", "all", Some("name"), Some("Barren"), Some(Coordinates(1, 2)), Some(Coordinates(3, 4))).query() shouldBe JsObject(
          Map(
            "galaxyName" -> Json.toJson("galaxyName"),
            "entityType" -> Json.toJson(Entity.planet)
          )
        )
      }
    }

    "throw an InvalidQueryException" when {

      "querying by name without name data" in {
        the[InvalidQueryException] thrownBy fullQuery.copy(select = "name", name = None).query() should have message "Missing data or Invalid Select for 'name'"
      }

      "querying by type without type data" in {
        the[InvalidQueryException] thrownBy fullQuery.copy(select = "type",planetType = None).query() should have message "Missing data or Invalid Select for 'type'"
      }

      "querying by location without coordinate data" in {
        the[InvalidQueryException] thrownBy fullQuery.copy(select = "location",galacticCoordinates = None,
          systemCoordinates = None).query() should have message "Missing data or Invalid Select for 'location'"
      }

      "querying by location with only system coordinates data" in {
        the[InvalidQueryException] thrownBy fullQuery.copy(select = "location",
          galacticCoordinates = None).query() should have message "Missing data or Invalid Select for 'location'"

      }

      "querying by an invalid type" in {
        the[InvalidQueryException] thrownBy fullQuery.copy(select = "fakeSelect").query() should have message "Missing data or Invalid Select for 'fakeSelect'"
      }
    }
  }
}
