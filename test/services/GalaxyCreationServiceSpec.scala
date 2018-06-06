package services

import helpers.UnitSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json

class GalaxyCreationServiceSpec extends UnitSpec with GuiceOneAppPerSuite {

  "The create galaxy method" should {
    lazy val service = app.injector.instanceOf[GalaxyCreationService]

    "return a valid galaxy" in {
      Json.toJson(service.createGalaxy("testGalaxy", 20, 25, 25, 5)) shouldBe ""
    }
  }
}
