package models.requests

import helpers.UnitSpec
import models.location.Coordinates
import play.api.libs.json.{JsObject, Json}

class SystemQueryRequestSpec extends UnitSpec {

  "The SystemQueryRequest" should {

    "return the correct Json query" when {

      "querying without coordinates data" in {
        SystemQueryRequest("galaxyName").query() shouldBe JsObject(
          Map(
            "galaxyName" -> Json.toJson("galaxyName")
          )
        )
      }

      "querying with the coordinates data" in {
        SystemQueryRequest("galaxyName", Some(Coordinates(1, 2))).query() shouldBe JsObject(
          Map(
            "galaxyName" -> Json.toJson("galaxyName"),
            "location.galactic" -> Json.toJson(Coordinates(1, 2))
          )
        )
      }
    }
  }
}
