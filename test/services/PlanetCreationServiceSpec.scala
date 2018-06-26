package services

import helpers.{EntityHelper, UnitSpec}
import models.PlanetType.{Barren, Desert, GasGiant, Island, Plains}
import models.attributes.Attributes
import models.entities.{PlanetEntity, StarEntity}
import models.location.Coordinates
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar

class PlanetCreationServiceSpec extends UnitSpec with MockitoSugar {

  def setupService(randomSize: Int = 0,
                   randomPrimary: Int = 0,
                   randomAtmosphere: Int = 0,
                   randomBiosphere: Int = 0): PlanetCreationService = {
    val mockRandomService = mock[RandomService]

    when(mockRandomService.generateRandomInteger(ArgumentMatchers.eq(6), ArgumentMatchers.eq(0)))
      .thenReturn(randomPrimary)

    when(mockRandomService.generateRandomInteger(ArgumentMatchers.eq(6), ArgumentMatchers.eq(1)))
      .thenReturn(randomSize)

    when(mockRandomService.generateRandomInteger(ArgumentMatchers.eq(10), ArgumentMatchers.eq(1)))
      .thenReturn(randomSize + 4)

    when(mockRandomService.generateRandomInteger(ArgumentMatchers.eq(0), ArgumentMatchers.eq(0)))
      .thenReturn(0)

    when(mockRandomService.generateRandomInteger(ArgumentMatchers.eq(6), ArgumentMatchers.eq(6)))
      .thenReturn(6)

    if (randomSize !=6) when(mockRandomService.generateRandomInteger(ArgumentMatchers.eq(randomSize), ArgumentMatchers.eq(0)))
      .thenReturn(randomAtmosphere)

    when(mockRandomService.generateRandomInteger(ArgumentMatchers.eq(3), ArgumentMatchers.eq(0)))
      .thenReturn(randomBiosphere)

    new PlanetCreationService(mockRandomService)
  }

  "Calling .valueOrMin" should {
    val service = setupService(1, 1)

    "return the minimum" when {

      "the value is less than the minimum" in {
        service.valueOrMin(0, 1) shouldBe 1
      }
    }

    "return the value" when {

      "the value is equal to the minimum" in {
        service.valueOrMin(4, 4) shouldBe 4
      }

      "the value is greater than the minimum" in {
        service.valueOrMin(6, 5) shouldBe 6
      }
    }
  }

  "Calling .valueOrMax" should {
    val service = setupService(1, 1)

    "return the maximum" when {

      "the value is greater than the minimum" in {
        service.valueOrMax(2, 1) shouldBe 1
      }
    }

    "return the value" when {

      "the value is equal to the maximum" in {
        service.valueOrMax(4, 4) shouldBe 4
      }

      "the value is less than the minimum" in {
        service.valueOrMax(5, 6) shouldBe 5
      }
    }
  }

  "Calling generatePrimaryAttributes" should {

    "return a valid array of attributes" when {

      "provided with coordinates less than 7 from the origin" in {
        val service = setupService(4, 3)
        val result = service.generatePrimaryAttributes(Coordinates(1, 2))

        result shouldBe Attributes.emptyAttributes
          .addOrUpdate(Attributes.size, 4)
          .addOrUpdate(Attributes.radioactivity, 3)
          .addOrUpdate(Attributes.geology, 3)
          .addOrUpdate(Attributes.minerals, 3)
      }

      "provided with coordinates greater than 7 from the origin" in {
        val service = setupService(6, 2)
        val result = service.generatePrimaryAttributes(Coordinates(10, 10))

        result shouldBe Attributes.emptyAttributes
          .addOrUpdate(Attributes.size, 10)
          .addOrUpdate(Attributes.radioactivity, 2)
          .addOrUpdate(Attributes.geology, 2)
          .addOrUpdate(Attributes.minerals, 2)
      }
    }
  }

  "Calling atmosphereCondition" should {
    val service = setupService()

    "return a value of (6, 6)" when {

      "provided with a size of 7" in {
        service.atmosphereCondition(Attributes.emptyAttributes.addOrUpdate(Attributes.size, 7)) shouldBe(6, 6)
      }

      "provided with a size greater than 7" in {
        service.atmosphereCondition(Attributes.emptyAttributes.addOrUpdate(Attributes.size, 8)) shouldBe(6, 6)
      }
    }

    "return a value of (size, 0)" when {

      "provided with a size of 2" in {
        service.atmosphereCondition(Attributes.emptyAttributes.addOrUpdate(Attributes.size, 2)) shouldBe(2, 0)
      }

      "provided with a size less than 2" in {
        service.atmosphereCondition(Attributes.emptyAttributes.addOrUpdate(Attributes.size, 1)) shouldBe(1, 0)
      }
    }

    "return a value of (6, 1)" when {

      "provided with a size of 3" in {
        service.atmosphereCondition(Attributes.emptyAttributes.addOrUpdate(Attributes.size, 3)) shouldBe(6, 1)
      }

      "provided with a size of 6" in {
        service.atmosphereCondition(Attributes.emptyAttributes.addOrUpdate(Attributes.size, 6)) shouldBe(6, 1)
      }
    }
  }

  "Calling temperatureCondition" should {
    val service = setupService()

    "return a value of (4, 0)" when {

      "a long distance away with a low modifier" in {
        service.temperatureCondition(StarEntity("", "", "", EntityHelper.validStarAttributes(3, "Red"), Coordinates(1, 2), 300),
          Coordinates(10, 10))(Attributes.emptyAttributes
          .addOrUpdate(Attributes.geology, 4)
          .addOrUpdate(Attributes.atmosphere, 4)) shouldBe(4, 0)
      }
    }

    "return a value of (6, 6)" when {

      "close by with a high modifier" in {
        service.temperatureCondition(StarEntity("", "", "", EntityHelper.validStarAttributes(7, "Red"), Coordinates(1, 2), 300),
          Coordinates(3, 1))(Attributes.emptyAttributes
          .addOrUpdate(Attributes.geology, 4)
          .addOrUpdate(Attributes.atmosphere, 4)) shouldBe(6, 6)
      }
    }
  }

  "Calling waterCondition" should {
    val service = setupService()

    "return a value of (0, 0)" when {

      "provided with an atmosphere of 0" in {
        service.waterCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.atmosphere, 0)
          .addOrUpdate(Attributes.temperature, 4)) shouldBe(0, 0)
      }

      "provided with a temperature of 5" in {
        service.waterCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.atmosphere, 1)
          .addOrUpdate(Attributes.temperature, 5)) shouldBe(0, 0)
      }

      "provided with a temperature of 6" in {
        service.waterCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.atmosphere, 1)
          .addOrUpdate(Attributes.temperature, 6)) shouldBe(0, 0)
      }
    }

    "return a value of (6, 0)" when {

      "provided with an atmosphere of 1 and temperature of 4" in {
        service.waterCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.atmosphere, 1)
          .addOrUpdate(Attributes.temperature, 4)) shouldBe(6, 0)
      }

      "provided with an atmosphere of 2 and temperature of 3" in {
        service.waterCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.atmosphere, 2)
          .addOrUpdate(Attributes.temperature, 3)) shouldBe(6, 0)
      }
    }
  }

  "Calling biosphereCondition" should {
    val service = setupService()

    "return a value of (6, 1)" when {

      "provided with a water value of 2 and radioactivity and geology values of 3" in {
        service.biosphereCondition(Attributes.emptyAttributes
        .addOrUpdate(Attributes.water, 2)
        .addOrUpdate(Attributes.radioactivity, 3)
        .addOrUpdate(Attributes.geology, 3)) shouldBe (6, 1)
      }

      "provided with a water value of 3 and radioactivity and geology values of 4" in {
        service.biosphereCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 3)
          .addOrUpdate(Attributes.radioactivity, 4)
          .addOrUpdate(Attributes.geology, 4)) shouldBe (6, 1)
      }
    }

    "return a value of (3, 0)" when {

      "provided with a water value of 1 and radioactivity and geology values of 3" in {
        service.biosphereCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 1)
          .addOrUpdate(Attributes.radioactivity, 3)
          .addOrUpdate(Attributes.geology, 3)) shouldBe (3, 0)
      }

      "provided with a water value of 1 and radioactivity and geology values of 4" in {
        service.biosphereCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 1)
          .addOrUpdate(Attributes.radioactivity, 4)
          .addOrUpdate(Attributes.geology, 4)) shouldBe (3, 0)
      }
    }

    "return a value of (0, 0)" when {

      "provided with a water value of 2, radioactivity value of 4 and geology value of 5" in {
        service.biosphereCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 2)
          .addOrUpdate(Attributes.radioactivity, 4)
          .addOrUpdate(Attributes.geology, 5)) shouldBe (0, 0)
      }

      "provided with a water value of 2, radioactivity value of 5 and geology value of 4" in {
        service.biosphereCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 2)
          .addOrUpdate(Attributes.radioactivity, 5)
          .addOrUpdate(Attributes.geology, 4)) shouldBe (0, 0)
      }

      "provided with a water value of 1, radioactivity value of 4 and geology value of 5" in {
        service.biosphereCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 1)
          .addOrUpdate(Attributes.radioactivity, 4)
          .addOrUpdate(Attributes.geology, 5)) shouldBe (0, 0)
      }

      "provided with a water value of 1, radioactivity value of 5 and geology value of 4" in {
        service.biosphereCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 1)
          .addOrUpdate(Attributes.radioactivity, 5)
          .addOrUpdate(Attributes.geology, 4)) shouldBe (0, 0)
      }

      "provided with a water value of 0 and radioactivity and geology values of 4" in {
        service.biosphereCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 0)
          .addOrUpdate(Attributes.radioactivity, 4)
          .addOrUpdate(Attributes.geology, 4)) shouldBe (0, 0)
      }
    }
  }

  "Calling toxicityCondition" should {
    val service = setupService()

    "return a value of (6, 0)" when {

      "provided with an atmosphere value of 1" in {
        service.toxicityCondition(Attributes.emptyAttributes.addOrUpdate(Attributes.atmosphere, 1)) shouldBe (6, 0)
      }

      "provided with an atmosphere value of -1" in {
        service.toxicityCondition(Attributes.emptyAttributes.addOrUpdate(Attributes.atmosphere, -1)) shouldBe (6, 0)
      }
    }

    "return a value of (0, 0)" when {

      "provided with an atmosphere value of 0" in {
        service.toxicityCondition(Attributes.emptyAttributes.addOrUpdate(Attributes.atmosphere, 0)) shouldBe (0, 0)
      }
    }
  }

  "Calling breathableCondition" should {
    val service = setupService()

    "return a value of (6, 0)" when {

      "provided with a biosphere of 1 and an atmosphere of 1" in {
        service.breathableCondition(Attributes.emptyAttributes
        .addOrUpdate(Attributes.biosphere, 1)
        .addOrUpdate(Attributes.atmosphere, 1)) shouldBe (6, 0)
      }

      "provided with a biosphere of 2 and an atmosphere of 5" in {
        service.breathableCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.biosphere, 2)
          .addOrUpdate(Attributes.atmosphere, 5)) shouldBe (6, 0)
      }
    }

    "return a value of (0, 0)" when {

      "provided with a biosphere of 0 and an atmosphere of 1" in {
        service.breathableCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.biosphere, 0)
          .addOrUpdate(Attributes.atmosphere, 1)) shouldBe (0, 0)
      }

      "provided with a biosphere of 1 and an atmosphere of 0" in {
        service.breathableCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.biosphere, 1)
          .addOrUpdate(Attributes.atmosphere, 0)) shouldBe (0, 0)
      }

      "provided with a biosphere of 1 and an atmosphere of 6" in {
        service.breathableCondition(Attributes.emptyAttributes
          .addOrUpdate(Attributes.biosphere, 1)
          .addOrUpdate(Attributes.atmosphere, 6)) shouldBe (0, 0)
      }
    }
  }

  "Calling dangerCondition" should {
    val service = setupService()

    "return a value of (6, 0)" when {

      "provided with a breathable value of 2" in {
        service.dangerCondition(Attributes.emptyAttributes.addOrUpdate(Attributes.breathable, 2)) shouldBe (6, 0)
      }

      "provided with a breathable value of 3" in {
        service.dangerCondition(Attributes.emptyAttributes.addOrUpdate(Attributes.breathable, 3)) shouldBe (6, 0)
      }
    }

    "return a value of (0, 0)" when {

      "provided with a breathable value of 1" in {
        service.dangerCondition(Attributes.emptyAttributes.addOrUpdate(Attributes.breathable, 1)) shouldBe (0, 0)
      }

      "provided with a breathable value of 0" in {
        service.dangerCondition(Attributes.emptyAttributes.addOrUpdate(Attributes.breathable, 0)) shouldBe (0, 0)
      }
    }
  }

  "Calling generateSecondaryAttributes" should {

    "return a valid set of attributes" when {

      "a small sized planet is generated close to the star" in {
        setupService(2, 1, 1, 0).generateSecondaryAttributes(
          StarEntity("", "", "", EntityHelper.validStarAttributes(3, "Red"), Coordinates(1, 2), 200),
          Coordinates(3, 3)
        ) shouldBe Attributes.emptyAttributes
            .addOrUpdate(Attributes.toxicity, 1)
            .addOrUpdate(Attributes.size, 2)
            .addOrUpdate(Attributes.radioactivity, 1)
            .addOrUpdate(Attributes.geology, 1)
            .addOrUpdate(Attributes.atmosphere, 1)
            .addOrUpdate(Attributes.temperature, 0)
            .addOrUpdate(Attributes.minerals, 1)
            .addOrUpdate(Attributes.water, 1)
            .addOrUpdate(Attributes.biosphere, 0)
            .addOrUpdate(Attributes.breathable, 0)
            .addOrUpdate(Attributes.danger, 0)
      }

      "a large planet is generated far away from the star" in {
        setupService(5, 2, 1, 1).generateSecondaryAttributes(
          StarEntity("", "", "", EntityHelper.validStarAttributes(8, "Red"), Coordinates(1, 2), 200),
          Coordinates(7, 10)
        ) shouldBe Attributes.emptyAttributes
          .addOrUpdate(Attributes.toxicity, 2)
          .addOrUpdate(Attributes.size, 9)
          .addOrUpdate(Attributes.radioactivity, 2)
          .addOrUpdate(Attributes.geology, 2)
          .addOrUpdate(Attributes.atmosphere, 6)
          .addOrUpdate(Attributes.temperature, 0)
          .addOrUpdate(Attributes.minerals, 2)
          .addOrUpdate(Attributes.water, 2)
          .addOrUpdate(Attributes.biosphere, 5)
          .addOrUpdate(Attributes.breathable, 0)
          .addOrUpdate(Attributes.danger, 0)
      }
    }
  }

  "Calling the .generateType method" should {
    val service = new PlanetCreationService(new RandomService)

    "return an extreme planet type" when {

      "attributes matching a gas giant are used" in {
        service.generateType(Attributes.emptyAttributes.addOrUpdate(Attributes.size, 7)) shouldBe GasGiant
      }
    }

    "return a habitable planet type" when {
      val habitableConditions = Attributes.emptyAttributes
        .addOrUpdate(Attributes.temperature, 2)
        .addOrUpdate(Attributes.geology, 0)
        .addOrUpdate(Attributes.radioactivity, 0)
        .addOrUpdate(Attributes.toxicity, 0)
        .addOrUpdate(Attributes.atmosphere, 2)
        .addOrUpdate(Attributes.biosphere, 2)
        .addOrUpdate(Attributes.breathable, 2)
        .addOrUpdate(Attributes.water, 0)

      "attributes matching a habitable planet from the list are used" in {
        service.generateType(habitableConditions.addOrUpdate(Attributes.water, 5)) shouldBe Island
      }

      "attributes not matching a habitable planet from the list are used" in {
        service.generateType(habitableConditions) shouldBe Plains
      }
    }

    "return a main planet type" when {

      "attributes not matching a habitable or extreme world are used" in {
        service.generateType(Attributes.emptyAttributes.addOrUpdate(Attributes.water, 0)) shouldBe Desert
      }
    }

    "return a Barren planet type" when {

      "attributes not matching any planet type are used" in {
        service.generateType(Attributes.emptyAttributes) shouldBe Barren
      }
    }
  }

  "Calling the .createPlanet method" should {
    val service = new PlanetCreationService(new RandomService)
    val largeStar = StarEntity("", "", "", EntityHelper.validStarAttributes(8, "Blue"), Coordinates(1, 2), 800)
    val smallStar = StarEntity("", "", "", EntityHelper.validStarAttributes(2, "White"), Coordinates(1, 2), 800)

    "return a valid planet" when {

      "close to a large star" in {
        service.createPlanet("", largeStar, Coordinates(3, 2)) shouldBe a[PlanetEntity]
      }

      "close to a small star" in {
        service.createPlanet("", smallStar, Coordinates(3, 2)) shouldBe a[PlanetEntity]
      }

      "far from a large star" in {
        service.createPlanet("", largeStar, Coordinates(10, 8)) shouldBe a[PlanetEntity]
      }

      "far from a small star" in {
        service.createPlanet("", smallStar, Coordinates(10, 8)) shouldBe a[PlanetEntity]
      }
    }
  }
}
