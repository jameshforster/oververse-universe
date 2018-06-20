package controllers

import helpers.UnitSpec
import models.requests.GenerateGalaxyRequest
import org.mockito.ArgumentMatchers
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.ControllerComponents
import services.GalaxyCreationService
import org.mockito.Mockito._
import play.api.libs.json.Json
import reactivemongo.api.commands.UpdateWriteResult
import play.api.http.Status._

import scala.concurrent.Future

class AdminControllerSpec extends UnitSpec with MockitoSugar with GuiceOneAppPerSuite {

  def createTestController(success: Boolean): AdminController = {
    val components = fakeApplication().injector.instanceOf[ControllerComponents]
    val service = mock[GalaxyCreationService]

    if (success) {
      when(service.saveAndCreateGalaxy(ArgumentMatchers.any[String], ArgumentMatchers.any[Int], ArgumentMatchers.any[Int], ArgumentMatchers.any[Int], ArgumentMatchers.any[Int]))
        .thenReturn(Future.successful(mock[UpdateWriteResult]))
    } else {
      when(service.saveAndCreateGalaxy(ArgumentMatchers.any[String], ArgumentMatchers.any[Int], ArgumentMatchers.any[Int], ArgumentMatchers.any[Int], ArgumentMatchers.any[Int]))
        .thenReturn(Future.failed(new Exception("test error")))
    }

    new AdminController(components, service)
  }

  "The generateGalaxy request" should {

    "return an OK" when {

      "the service does not return a fail" in {
        val result = createTestController(true).generateGalaxy()(fakeRequestWithBody(Json.toJson(GenerateGalaxyRequest("name", 5, 1, 1, 1))))

        statusOf(result) shouldBe OK
        bodyOf(result) shouldBe "The name galaxy has been created!"
      }
    }

    "return an exception" when {

      "the service returns a failure" in {
        val result = createTestController(false).generateGalaxy()(fakeRequestWithBody(Json.toJson(GenerateGalaxyRequest("name", 5, 1, 1, 1))))

        the[Exception] thrownBy await(result) should have message "test error"
      }
    }
  }
}
