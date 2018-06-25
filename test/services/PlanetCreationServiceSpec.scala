package services

import helpers.UnitSpec
import models.attributes.Attributes
import models.location.Coordinates
import org.mockito.ArgumentMatchers
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

class PlanetCreationServiceSpec extends UnitSpec with MockitoSugar {

  def setupService(randomSize: Int, randomPrimary: Int): PlanetCreationService = {
    val mockRandomService = mock[RandomService]

    when(mockRandomService.generateRandomInteger(ArgumentMatchers.eq(6), ArgumentMatchers.eq(0)))
      .thenReturn(randomPrimary)

    when(mockRandomService.generateRandomInteger(ArgumentMatchers.eq(6), ArgumentMatchers.eq(1)))
      .thenReturn(randomSize)

    when(mockRandomService.generateRandomInteger(ArgumentMatchers.eq(10), ArgumentMatchers.eq(1)))
      .thenReturn(randomSize + 4)

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
}
