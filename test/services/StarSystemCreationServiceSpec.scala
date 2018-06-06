package services

import helpers.UnitSpec
import models.StarSystem
import models.attributes.ColourAttribute
import models.location.Coordinates
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class StarSystemCreationServiceSpec extends UnitSpec with GuiceOneAppPerSuite {

  "The create system method" should {

    "return a valid system" when {

      "creating a normal star type" in {
        val mockRandomService = new RandomService {
          override def selectRandomElement[A](seq: Seq[A]): Option[A] = {
            if (seq.forall(_.isInstanceOf[ColourAttribute])) Some(ColourAttribute.red.asInstanceOf[A])
            else super.selectRandomElement(seq)
          }
        }
        val service = new StarSystemCreationService(mockRandomService, app.injector.instanceOf[PlanetCreationService])

        service.createSystem(Coordinates(1, 1), 2) shouldBe a [StarSystem]
      }
    }
  }
}
