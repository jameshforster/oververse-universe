package models.location

import helpers.UnitSpec

class CoordinatesSpec extends UnitSpec {

  "The distanceFromPoint method" should {

    "return the correct distance" when {

      "supplied with two points that are the same coordinates" in {
        Coordinates(0, 0).distanceFromPoint(Coordinates(0, 0)) shouldBe 0
      }
    }
  }
}
