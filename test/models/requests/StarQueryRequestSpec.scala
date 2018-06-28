package models.requests

import helpers.UnitSpec
import models.entities.Entity
import models.exceptions.InvalidQueryException
import models.location.Coordinates
import play.api.libs.json.{JsObject, Json}

class StarQueryRequestSpec extends UnitSpec {

  "The StarQueryRequest" should {

    val fullQuery = StarQueryRequest("galaxyName", "all", Some("name"), Some("category"), Some(Coordinates(1, 2)))

    "return the correct Json query" when {

      "querying by name with name data" in {
        StarQueryRequest("galaxyName", "name", name = Some("name")).query() shouldBe JsObject(
          Map(
            "galaxyName" -> Json.toJson("galaxyName"),
            "entityType" -> Json.toJson(Entity.star),
            "name" -> Json.toJson("name")
          )
        )
      }

      "querying by category with category data" in {
        StarQueryRequest("galaxyName", "category", category = Some("Red Giant")).query() shouldBe JsObject(
          Map(
            "galaxyName" -> Json.toJson("galaxyName"),
            "entityType" -> Json.toJson(Entity.star),
            "attributes.attributes.category" -> Json.toJson("Red Giant")
          )
        )
      }

      "querying by location with coordinates data" in {
        StarQueryRequest("galaxyName", "location", galacticCoordinates = Some(Coordinates(1, 2))).query() shouldBe JsObject(
          Map(
            "galaxyName" -> Json.toJson("galaxyName"),
            "entityType" -> Json.toJson(Entity.star),
            "location.galactic" -> Json.toJson(Coordinates(1, 2))
          )
        )
      }

      "querying by all with no data" in {
        StarQueryRequest("galaxyName", "all").query() shouldBe JsObject(
          Map(
            "galaxyName" -> Json.toJson("galaxyName"),
            "entityType" -> Json.toJson(Entity.star)
          )
        )
      }

      "querying by all with all data" in {
        fullQuery.query() shouldBe JsObject(
          Map(
            "galaxyName" -> Json.toJson("galaxyName"),
            "entityType" -> Json.toJson(Entity.star)
          )
        )
      }
    }

    "throw an InvalidQueryException" when {

      "querying by name without name data" in {
        the[InvalidQueryException] thrownBy fullQuery.copy(select = "name", name = None).query() should have message "Missing data or Invalid Select for 'name'"
      }

      "querying by category without category data" in {
        the[InvalidQueryException] thrownBy fullQuery.copy(select = "category", category = None).query() should have message "Missing data or Invalid Select for 'category'"
      }

      "querying by location without coordinate data" in {
        the[InvalidQueryException] thrownBy fullQuery.copy(select = "location",galacticCoordinates = None)
          .query() should have message "Missing data or Invalid Select for 'location'"
      }

      "querying by an invalid type" in {
        the[InvalidQueryException] thrownBy fullQuery.copy(select = "fakeSelect").query() should have message "Missing data or Invalid Select for 'fakeSelect'"
      }
    }
  }
}
