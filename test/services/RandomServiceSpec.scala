package services

import helpers.UnitSpec

class RandomServiceSpec extends UnitSpec {

  val randomService = new RandomService

  "Calling .generateRandomInteger" should {

    "generate a completely random integer" when {

      "provided with no minimum value" in {
        val results = for (n <- 0 to 99) yield randomService.generateRandomInteger(10)

        results.forall(result => (0 to 10).contains(result)) shouldBe true
      }

      "provided with a minimum value" in {
        val results = for (n <- 0 to 99) yield randomService.generateRandomInteger(8, 4)

        results.forall(result => (4 to 8).contains(result)) shouldBe true
      }
    }
  }

  "Calling .selectRandomElement" should {

    "select one random element from a collection" when {

      "supplied with any collection" in {
        val result = randomService.selectRandomElement(0 to 10)

        (0 to 10).contains(result.get) shouldBe true
      }
    }

    "return a none" when {

      "supplied with an empty collection" in {
        val result = randomService.selectRandomElement(Seq())

        result shouldBe None
      }
    }
  }

  "Calling .generateId" should {

    "produce a random string each time" in {
      val result = randomService.generateId()
      val results = for (n <- 0 to 99) yield randomService.generateId()

      result.length shouldBe 36

      results.forall(result => results.count(_.equals(result)) == 1) shouldBe true
    }
  }
}
