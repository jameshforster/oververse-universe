package controllers

import helpers.{EntityHelper, UnitSpec}
import models.{GalaxyModel, StarSystem}
import models.entities.{PlanetEntity, StarEntity}
import models.location.{Coordinates, Location}
import models.requests.{PlanetQueryRequest, StarQueryRequest, SystemQueryRequest}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import services.QueryService
import play.api.http.Status._

import scala.concurrent.Future

class QueryUniverseControllerSpec extends UnitSpec with GuiceOneAppPerSuite with MockitoSugar {

  val testPlanet = PlanetEntity("testId", "testGalaxyName", "testName", EntityHelper.validPlanetAttributes, Coordinates(1, 1), Coordinates(2, 2), 50)
  val testStar = StarEntity("testId", "testGalaxyName", "testName", EntityHelper.validStarAttributes(4, "Red"), Coordinates(1, 1), 400)
  val testSystem = StarSystem(testStar, Seq(testPlanet), Seq(), Seq())
  val testGalaxy = GalaxyModel("testGalaxyName", 5, active = false, test = true)

  def createTestController(success: Boolean): QueryUniverseController = {

    val components = fakeApplication().injector.instanceOf[ControllerComponents]
    val service = mock[QueryService]

    if (success) {
      when(service.queryPlanets(ArgumentMatchers.any[PlanetQueryRequest]))
        .thenReturn(Future.successful(Seq(testPlanet)))
      when(service.queryStars(ArgumentMatchers.any[StarQueryRequest]))
        .thenReturn(Future.successful(Seq(testStar)))
      when(service.querySystems(ArgumentMatchers.any[SystemQueryRequest]))
        .thenReturn(Future.successful(Seq(testSystem)))
      when(service.getGalaxies)
        .thenReturn(Future.successful(Seq(testGalaxy)))
    } else {
      when(service.queryPlanets(ArgumentMatchers.any[PlanetQueryRequest]))
        .thenReturn(Future.failed(new Exception("test error")))
      when(service.queryStars(ArgumentMatchers.any[StarQueryRequest]))
        .thenReturn(Future.failed(new Exception("test error")))
      when(service.querySystems(ArgumentMatchers.any[SystemQueryRequest]))
        .thenReturn(Future.failed(new Exception("test error")))
      when(service.getGalaxies)
        .thenReturn(Future.failed(new Exception("test error")))
    }

    new QueryUniverseController(components, service)
  }

  "Calling the .query method" should {

    "return an OK response" when {
      val controller = createTestController(true)

      "the service does not return a failure for a planet" in {
        val result = controller.query("planet")(fakeRequestWithBody(Json.toJson(PlanetQueryRequest("testGalaxyName", "name", Some("testName")))))

        statusOf(result) shouldBe OK
        bodyOf(result) shouldBe Json.toJson(Seq(testPlanet)).toString()
      }

      "the service does not return a failure for a star" in {
        val result = controller.query("star")(fakeRequestWithBody(Json.toJson(StarQueryRequest("testGalaxyName", "name", Some("testName")))))

        statusOf(result) shouldBe OK
        bodyOf(result) shouldBe Json.toJson(Seq(testStar)).toString()
      }

      "the service does not return a failure for a system" in {
        val result = controller.query("system")(fakeRequestWithBody(Json.toJson(SystemQueryRequest("testGalaxyName", Some(Coordinates(1, 1))))))

        statusOf(result) shouldBe OK
        bodyOf(result) shouldBe Json.toJson(Seq(testSystem)).toString()
      }
    }

    "return a BadRequest response" when {
      val controller = createTestController(true)

      "the method is called with a non-matching query" in {
        val result = controller.query("fakeType")(fakeRequestWithBody(Json.toJson(PlanetQueryRequest("testGalaxyName", "name", Some("testName")))))

        statusOf(result) shouldBe BAD_REQUEST
        bodyOf(result) shouldBe "Invalid query type: fakeType"
      }
    }

    "return an exception" when {
      val controller = createTestController(false)

      "the service returns a failure for a planet" in {
        val result = controller.query("planet")(fakeRequestWithBody(Json.toJson(PlanetQueryRequest("testGalaxyName", "name", Some("testName")))))

        the[Exception] thrownBy await(result) should have message "test error"
      }

      "the service returns a failure for a star" in {
        val result = controller.query("star")(fakeRequestWithBody(Json.toJson(StarQueryRequest("testGalaxyName", "name", Some("testName")))))

        the[Exception] thrownBy await(result) should have message "test error"
      }

      "the service returns a failure for a system" in {
        val result = controller.query("system")(fakeRequestWithBody(Json.toJson(SystemQueryRequest("testGalaxyName", Some(Coordinates(1, 1))))))

        the[Exception] thrownBy await(result) should have message "test error"
      }
    }
  }

  "Calling the .getGalaxies method" should {

    "return an Ok response" when {
      val controller = createTestController(true)

      "no errors occur" in {
        val result = controller.getGalaxies()(fakeRequest)

        statusOf(result) shouldBe OK
        bodyOf(result) shouldBe Json.toJson(Seq(testGalaxy)).toString()
      }
    }

    "return an exception" when {
      val controller = createTestController(false)

      "any errors occur" in {
        val result = controller.getGalaxies()(fakeRequest)

        the[Exception] thrownBy await(result) should have message "test error"
      }
    }
  }
}
