package models.location

import helpers.UnitSpec

class CoordinatesSpec extends UnitSpec {

  "The distanceFromPoint method" should {

    "return the correct distance" when {

      "supplied with two points that are the same coordinates" in {
        Coordinates(0, 0).distanceFromPoint(Coordinates(0, 0)) shouldBe 0
      }

      "supplied with two points that have an exact distance of 5 apart" in {
        Coordinates(2, 4).distanceFromPoint(Coordinates(-2, 7)) shouldBe 5
      }

      "supplied with two points that have a non-exact distance of 2 apart" in {
        Coordinates(-1, 4).distanceFromPoint(Coordinates(-2, 6)).toDouble shouldBe (2.23 +- 0.01)
      }
    }
  }

  "The distanceFromOrigin method" should {

    "return the correct distance" when {

      "supplied with a point at the origin" in {
        Coordinates(0, 0).distanceFromOrigin() shouldBe 0
      }

      "supplied with a point that has an exact distance of 5 from the origin" in {
        Coordinates(-3, 4).distanceFromOrigin() shouldBe 5
      }

      "supplied with a point that has a non-exact distance of 2 from the origin" in {
        Coordinates(1, -2).distanceFromOrigin().toDouble shouldBe (2.23 +- 0.01)
      }
    }
  }
}
