package services

import connectors.MongoConnector
import helpers.{EntityHelper, UnitSpec}
import models.StarSystem
import models.entities.{Entity, PlanetEntity, StarEntity, StationEntity}
import models.location.Coordinates
import models.requests.{PlanetQueryRequest, StarQueryRequest, SystemQueryRequest}
import org.mockito.ArgumentMatchers
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import play.api.libs.json.{JsObject, Reads}

import scala.concurrent.Future

class QueryServiceSpec extends UnitSpec with MockitoSugar {
  val testPlanet: PlanetEntity = PlanetEntity("id", "galaxyName", "name", EntityHelper.validPlanetAttributes, Coordinates(1, 2), Coordinates(3,4), 10)
  val testPlanet2: PlanetEntity = testPlanet.copy(galacticCoordinates = Coordinates(2, 3))
  val testStar: StarEntity = StarEntity("id", "galaxyName", "name", EntityHelper.validStarAttributes(5, "Red"), Coordinates(1, 2), 500)
  val testStar2: StarEntity = testStar.copy(coordinates = Coordinates(2, 3))
  val testStation: StationEntity = StationEntity("id", "galaxyName", "name", EntityHelper.validStationAttributes, Coordinates(1, 2), Coordinates(3,4), 10)

  def setupService(response: Future[Seq[Entity]]): QueryService = {
    val mockMongoConnector = mock[MongoConnector]

    when(mockMongoConnector.findAllData[Entity](ArgumentMatchers.eq("entities"), ArgumentMatchers.any[JsObject])(ArgumentMatchers.any[Reads[Entity]]))
      .thenReturn(response)

    new QueryService(mockMongoConnector)
  }

  "Calling .queryPlanets" should {

    "return a valid list of planets" when {

      "a single matching planet is found" in {
        val result = setupService(Future.successful(Seq(testPlanet))).queryPlanets(PlanetQueryRequest("galaxyName", "all"))

        await(result) shouldBe Seq(testPlanet)
      }

      "multiple matching planets are found" in {
        val result = setupService(Future.successful(Seq(testPlanet, testPlanet, testPlanet))).queryPlanets(PlanetQueryRequest("galaxyName", "all"))

        await(result) shouldBe Seq(testPlanet, testPlanet, testPlanet)
      }

      "multiple non-planet matches are found" in {
        val result = setupService(Future.successful(Seq(testStar, testStar, testStar))).queryPlanets(PlanetQueryRequest("galaxyName", "all"))

        await(result) shouldBe Seq()
      }

      "a combination of planets and non-planet matches are found" in {
        val result = setupService(Future.successful(Seq(testPlanet, testStar, testPlanet, testStar))).queryPlanets(PlanetQueryRequest("galaxyName", "all"))

        await(result) shouldBe Seq(testPlanet, testPlanet)
      }
    }
  }

  "Calling .queryStars" should {

    "return a valid list of stars" when {

      "a single matching star is found" in {
        val result = setupService(Future.successful(Seq(testStar))).queryStars(StarQueryRequest("galaxyName", "all"))

        await(result) shouldBe Seq(testStar)
      }

      "multiple matching stars are found" in {
        val result = setupService(Future.successful(Seq(testStar, testStar, testStar))).queryStars(StarQueryRequest("galaxyName", "all"))

        await(result) shouldBe Seq(testStar, testStar, testStar)
      }

      "multiple non-star matches are found" in {
        val result = setupService(Future.successful(Seq(testPlanet, testPlanet, testPlanet))).queryStars(StarQueryRequest("galaxyName", "all"))

        await(result) shouldBe Seq()
      }

      "a combination of stars and non-planet matches are found" in {
        val result = setupService(Future.successful(Seq(testStar, testPlanet, testStar, testPlanet))).queryStars(StarQueryRequest("galaxyName", "all"))

        await(result) shouldBe Seq(testStar, testStar)
      }
    }
  }

  "Calling .querySystems" should {

    "return a valid list of systems" when {

      "no matching stars are found" in {
        val result = setupService(Future.successful(Seq(testPlanet, testPlanet))).querySystems(SystemQueryRequest("galaxyName"))

        await(result) shouldBe Seq()
      }

      "one matching star is found without orbitals" in {
        val result = setupService(Future.successful(Seq(testStar, testPlanet2))).querySystems(SystemQueryRequest("galaxyName"))

        await(result) shouldBe Seq(StarSystem(testStar, Seq(), Seq(), Seq()))
      }

      "one matching star is found with orbitals" in {
        val result = setupService(Future.successful(Seq(testStar, testPlanet, testPlanet, testStation))).querySystems(SystemQueryRequest("galaxyName"))

        await(result) shouldBe Seq(StarSystem(testStar, Seq(testPlanet, testPlanet), Seq(testStation), Seq()))
      }

      "multiple matching stars are found" in {
        val result = setupService(Future.successful(Seq(testStar, testPlanet, testStar2, testPlanet2, testStation, testPlanet2))).querySystems(SystemQueryRequest("galaxyName"))

        await(result) shouldBe Seq(
          StarSystem(testStar, Seq(testPlanet), Seq(testStation), Seq()),
          StarSystem(testStar2, Seq(testPlanet2, testPlanet2), Seq(), Seq()))
      }
    }
  }
}
