package services

import helpers.UnitSpec
import models.Galaxy
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class GalaxyCreationServiceSpec extends UnitSpec with GuiceOneAppPerSuite {

  "The create galaxy method" should {
    lazy val service = app.injector.instanceOf[GalaxyCreationService]

    "return a valid galaxy" in {
      service.createGalaxy("testGalaxy", 5, 25, 25, 5) shouldBe a [Galaxy]
    }
  }
}
