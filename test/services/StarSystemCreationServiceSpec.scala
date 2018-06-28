package services

import helpers.{EntityHelper, UnitSpec}
import models.StarSystem
import models.attributes.{Attributes, ColourAttribute}
import models.entities.{Entity, PlanetEntity, StarEntity}
import models.location.Coordinates
import org.mockito.ArgumentMatchers
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

class StarSystemCreationServiceSpec extends UnitSpec with MockitoSugar {

  def testPlanet(coordinates: Coordinates) = {
    PlanetEntity("randomId", "galaxyName", "name", EntityHelper.validPlanetAttributes, Coordinates(1, 2), coordinates, 10)
  }

  def setupService(size: Int = 1, selectNonPlanetStar: Boolean = false): StarSystemCreationService = {
    val mockRandomService = new RandomService {
      override def generateId(): String = "randomId"

      override def generateRandomInteger(max: Int, min: Int): Int = {
        if (max == 10 && min == 1) size
        else if (selectNonPlanetStar && max == 10 && min == 0) 10
        else super.generateRandomInteger(max, min)
      }
    }

    val mockPlanetCreationService = mock[PlanetCreationService]
    val testPlanets = for (n <- 2 to 12) yield testPlanet(Coordinates(n, 0))

    when(mockPlanetCreationService.createPlanet(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(testPlanet(Coordinates(1, 0)), testPlanets: _*)

    new StarSystemCreationService(mockRandomService, mockPlanetCreationService)
  }

  "Calling createStar" should {

    "generate a star with dwarf colours" when {

      "a size of 1 is generated" in {
        val service = setupService()
        val result = service.createStar("galaxyName", Coordinates(1, 2))

        result shouldBe StarEntity(
          "randomId",
          "galaxyName",
          "Unnamed Star",
          Attributes.emptyAttributes
            .addOrUpdate(Attributes.size, 1)
            .addOrUpdate(Attributes.colour, result.colour.name),
          Coordinates(1, 2),
          100
        )
        ColourAttribute.dwarfColours.contains(result.colour) shouldBe true
      }

      "a size of 2 is generated" in {
        val service = setupService(2)
        val result = service.createStar("galaxyName", Coordinates(1, 2))

        result shouldBe StarEntity(
          "randomId",
          "galaxyName",
          "Unnamed Star",
          Attributes.emptyAttributes
            .addOrUpdate(Attributes.size, 2)
            .addOrUpdate(Attributes.colour, result.colour.name),
          Coordinates(1, 2),
          200
        )
        ColourAttribute.dwarfColours.contains(result.colour) shouldBe true
      }
    }

    "generate a star with main colours" when {

      "a size of 3 is generated" in {
        val service = setupService(3)
        val result = service.createStar("galaxyName", Coordinates(1, 2))

        result shouldBe StarEntity(
          "randomId",
          "galaxyName",
          "Unnamed Star",
          Attributes.emptyAttributes
            .addOrUpdate(Attributes.size, 3)
            .addOrUpdate(Attributes.colour, result.colour.name),
          Coordinates(1, 2),
          300
        )
        ColourAttribute.mainColours.exists(x => x._2 == result.colour) shouldBe true
      }

      "a size of 6 is generated" in {
        val service = setupService(6)
        val result = service.createStar("galaxyName", Coordinates(1, 2))

        result shouldBe StarEntity(
          "randomId",
          "galaxyName",
          "Unnamed Star",
          Attributes.emptyAttributes
            .addOrUpdate(Attributes.size, 6)
            .addOrUpdate(Attributes.colour, result.colour.name),
          Coordinates(1, 2),
          600
        )
        ColourAttribute.mainColours.exists(x => x._2 == result.colour) shouldBe true
      }
    }

    "generate a star with giant colours" when {

      "a size of 7 is generated" in {
        val service = setupService(7)
        val result = service.createStar("galaxyName", Coordinates(1, 2))

        result shouldBe StarEntity(
          "randomId",
          "galaxyName",
          "Unnamed Star",
          Attributes.emptyAttributes
            .addOrUpdate(Attributes.size, 7)
            .addOrUpdate(Attributes.colour, result.colour.name),
          Coordinates(1, 2),
          700
        )
        ColourAttribute.giantColours.contains(result.colour) shouldBe true
      }

      "a size of 9 is generated" in {
        val service = setupService(9)
        val result = service.createStar("galaxyName", Coordinates(1, 2))

        result shouldBe StarEntity(
          "randomId",
          "galaxyName",
          "Unnamed Star",
          Attributes.emptyAttributes
            .addOrUpdate(Attributes.size, 9)
            .addOrUpdate(Attributes.colour, result.colour.name),
          Coordinates(1, 2),
          900
        )
        ColourAttribute.giantColours.contains(result.colour) shouldBe true
      }
    }

    "generate a star with supergiant colours" when {

      "a size of 10 is generated" in {
        val service = setupService(10)
        val result = service.createStar("galaxyName", Coordinates(1, 2))

        result shouldBe StarEntity(
          "randomId",
          "galaxyName",
          "Unnamed Star",
          Attributes.emptyAttributes
            .addOrUpdate(Attributes.size, 10)
            .addOrUpdate(Attributes.colour, result.colour.name),
          Coordinates(1, 2),
          1000
        )
        ColourAttribute.superGiantColours.contains(result.colour) shouldBe true
      }
    }
  }

  "Calling createStarSystem" should {

    "create a valid star system without planets" when {

      "a neutron star is created" in {
        val service = setupService(9, selectNonPlanetStar = true)

        service.createSystem("galaxyName", Coordinates(1, 2), 100) shouldBe StarSystem(
          StarEntity("randomId", "galaxyName", "Unnamed Star", EntityHelper.validStarAttributes(9, "Neutron"), Coordinates(1, 2), 900),
          Seq(),
          Seq(),
          Seq()
        )
      }

      "a black hole is created" in {
        val service = setupService(10, selectNonPlanetStar = true)

        service.createSystem("galaxyName", Coordinates(1, 2), 100) shouldBe StarSystem(
          StarEntity("randomId", "galaxyName", "Unnamed Star", EntityHelper.validStarAttributes(10, "Black"), Coordinates(1, 2), 1000),
          Seq(),
          Seq(),
          Seq()
        )
      }

      "the chance of creating any planets is zero" in {
        val service = setupService(5)
        val result = service.createSystem("galaxyName", Coordinates(1, 2), 0)

        result shouldBe StarSystem(
          StarEntity("randomId", "galaxyName", "Unnamed Star", EntityHelper.validStarAttributes(5, result.stellarObject.attributes.getOrException[String](Attributes.colour)), Coordinates(1, 2), 500),
          Seq(),
          Seq(),
          Seq()
        )
      }
     }

    "create a valid star system with planets" when {

      "the chance of creating any planets is 100" in {
        val service = setupService(5)
        val result = service.createSystem("galaxyName", Coordinates(1, 2), 100)

        result.stellarObject shouldBe StarEntity("randomId", "galaxyName", "Unnamed Star", EntityHelper.validStarAttributes(5, result.stellarObject.attributes.getOrException[String](Attributes.colour)), Coordinates(1, 2), 500)
        result.majorOrbitals.size shouldBe 11
        result.minorOrbitals.isEmpty shouldBe true
        result.otherEntities.isEmpty shouldBe true
      }
    }
  }
}
