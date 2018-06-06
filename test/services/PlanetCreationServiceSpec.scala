package services

import helpers.UnitSpec
import models.attributes.Attributes
import models.entities.{PlanetEntity, StarEntity}
import models.location.Coordinates
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class PlanetCreationServiceSpec extends UnitSpec with GuiceOneAppPerSuite {

  "The create planet method" should {
    lazy val testService = new PlanetCreationService(new RandomService)

    "return a valid planet" when {

      "orbiting a large star" when {
        val attributes = Attributes.emptyAttributes
          .addOrUpdate[Int](Attributes.size, 10)
          .addOrUpdate[String](Attributes.colour, "Yellow")
        val fakeStar = StarEntity("star", attributes, Coordinates(4, 2), 1000)

        "close to the star" in {
          testService.createPlanet(fakeStar, Coordinates(1, 1), "testPlanet") shouldBe a [PlanetEntity]
        }

        "medium distance from the star" in {
          testService.createPlanet(fakeStar, Coordinates(6, 0), "testPlanet") shouldBe a [PlanetEntity]
        }

        "far away from the star" in {
          testService.createPlanet(fakeStar, Coordinates(10, 10), "testPlanet") shouldBe a [PlanetEntity]
        }
      }

      "orbiting a medium star" when {
        val attributes = Attributes.emptyAttributes
          .addOrUpdate[Int](Attributes.size, 5)
          .addOrUpdate[String](Attributes.colour, "Blue")
        val fakeStar = StarEntity("star", attributes, Coordinates(4, 2), 500)

        "close to the star" in {
          testService.createPlanet(fakeStar, Coordinates(1, 1), "testPlanet") shouldBe a [PlanetEntity]
        }

        "medium distance from the star" in {
          testService.createPlanet(fakeStar, Coordinates(6, 0), "testPlanet") shouldBe a [PlanetEntity]
        }

        "far away from the star" in {
          testService.createPlanet(fakeStar, Coordinates(10, 10), "testPlanet") shouldBe a [PlanetEntity]
        }
      }

      "orbiting a small star" when {
        val attributes = Attributes.emptyAttributes
          .addOrUpdate[Int](Attributes.size, 1)
          .addOrUpdate[String](Attributes.colour, "White")
        val fakeStar = StarEntity("star", attributes, Coordinates(4, 2), 100)

        "close to the star" in {
          testService.createPlanet(fakeStar, Coordinates(1, 1), "testPlanet") shouldBe a [PlanetEntity]
        }

        "medium distance from the star" in {
          testService.createPlanet(fakeStar, Coordinates(6, 0), "testPlanet") shouldBe a [PlanetEntity]
        }

        "far away from the star" in {
          testService.createPlanet(fakeStar, Coordinates(10, 10), "testPlanet") shouldBe a [PlanetEntity]
        }
      }
    }
  }
}
