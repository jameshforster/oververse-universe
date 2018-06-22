package models

import helpers.UnitSpec
import models.attributes.Attributes
import play.api.libs.json.Json

class PlanetTypeSpec extends UnitSpec {

  "The matching type method" should {
    val planetType = new PlanetType {
      override val name: String = "testType"
      override val conditions: Seq[Attributes => Boolean] = Seq(
        attributes => attributes.attributes.size == 2,
        attributes => attributes.getAttribute[Int]("test").nonEmpty
      )
    }

    "return a true" when {

      "all conditions pass" in {
        planetType.matchingType(Attributes.emptyAttributes
          .addOrUpdate("test", 1)
          .addOrUpdate("random", "")) shouldBe true
      }
    }

    "return a false" when {

      "the first condition fails" in {
        planetType.matchingType(Attributes.emptyAttributes
          .addOrUpdate("test", 1)) shouldBe false
      }

      "any other condition fails" in {
        planetType.matchingType(Attributes.emptyAttributes
          .addOrUpdate("fakeKey", 1)
          .addOrUpdate("random", "")) shouldBe false
      }
    }
  }

  "The habitableConditions" should {
    val minimumConditions = Attributes.emptyAttributes
      .addOrUpdate(Attributes.temperature, 2)
      .addOrUpdate(Attributes.geology, 0)
      .addOrUpdate(Attributes.radioactivity, 0)
      .addOrUpdate(Attributes.toxicity, 0)
      .addOrUpdate(Attributes.atmosphere, 2)
      .addOrUpdate(Attributes.biosphere, 2)
      .addOrUpdate(Attributes.breathable, 2)
      .addOrUpdate(Attributes.water, 0)

    val belowMinimumConditions = Attributes.emptyAttributes
      .addOrUpdate(Attributes.temperature, 1)
      .addOrUpdate(Attributes.geology, -1)
      .addOrUpdate(Attributes.radioactivity, -1)
      .addOrUpdate(Attributes.toxicity, -1)
      .addOrUpdate(Attributes.atmosphere, 1)
      .addOrUpdate(Attributes.biosphere, 1)
      .addOrUpdate(Attributes.breathable, 1)
      .addOrUpdate(Attributes.water, -1)

    val maximumConditions = Attributes.emptyAttributes
      .addOrUpdate(Attributes.temperature, 4)
      .addOrUpdate(Attributes.geology, 4)
      .addOrUpdate(Attributes.radioactivity, 2)
      .addOrUpdate(Attributes.toxicity, 2)
      .addOrUpdate(Attributes.atmosphere, 4)
      .addOrUpdate(Attributes.biosphere, 6)
      .addOrUpdate(Attributes.breathable, 6)
      .addOrUpdate(Attributes.water, 5)

    val aboveMaximumConditions = Attributes.emptyAttributes
      .addOrUpdate(Attributes.temperature, 5)
      .addOrUpdate(Attributes.geology, 5)
      .addOrUpdate(Attributes.radioactivity, 3)
      .addOrUpdate(Attributes.toxicity, 3)
      .addOrUpdate(Attributes.atmosphere, 5)
      .addOrUpdate(Attributes.biosphere, 7)
      .addOrUpdate(Attributes.breathable, 7)
      .addOrUpdate(Attributes.water, 6)

    "all be true" when {

      "all values are equal to their minimum value" in {
        PlanetType.habitableConditions.forall(_ (minimumConditions)) shouldBe true
      }

      "all values are equal to their maximum value" in {
        PlanetType.habitableConditions.forall(_ (maximumConditions)) shouldBe true
      }
    }

    "all be false" when {

      "all values are below their minimum value" in {
        PlanetType.habitableConditions.forall(condition => !condition(belowMinimumConditions)) shouldBe true
      }

      "all values are above their maximum value" in {
        PlanetType.habitableConditions.forall(condition => !condition(aboveMaximumConditions)) shouldBe true
      }

      "all values are not found" in {
        PlanetType.habitableConditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
      }
    }
  }

  "The PlanetType" should {
    val validJson = Json.parse(""""Plains"""")

    "be read from valid json correctly" in {
      Json.fromJson[PlanetType](validJson).get shouldBe PlanetType.Plains
    }

    "be written to json correctly" in {
      Json.toJson(PlanetType.Plains) shouldBe validJson
    }
  }

  "The attributeBetween method" should {

    "return a true" when {

      "the attribute is equal to the maximum" in {
        PlanetType.attributeBetween(6, 1)(6) shouldBe true
      }

      "the attribute is equal to the minimum" in {
        PlanetType.attributeBetween(6, 1)(1) shouldBe true
      }

      "the attribute lies between the maximum and minimum" in {
        PlanetType.attributeBetween(6, 1)(4) shouldBe true
      }
    }

    "return a false" when {

      "the attribute is greater than the maximum" in {
        PlanetType.attributeBetween(6, 1)(7) shouldBe false
      }

      "the attribute is less than the minimum" in {
        PlanetType.attributeBetween(6, 1)(0) shouldBe false
      }

      "the maximum and minimum are set to invalidate an attribute" in {
        PlanetType.attributeBetween(3, 1)(4) shouldBe false
      }
    }
  }

  "The Barren planetType" should {

    "have no conditions" in {
      PlanetType.Barren.conditions.size shouldBe 0
    }
  }

  "The Crater planetType" should {

    "pass all conditions" when {

      "the attributes have an atmosphere of 0" in {
        PlanetType.Crater.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.atmosphere, 0))) shouldBe true
      }
    }

    "fail all conditions" when {

      "the attributes have an atmosphere greater than 0" in {
        PlanetType.Crater.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.atmosphere, 1))) shouldBe false
      }

      "the attributes have an atmosphere less than 0" in {
        PlanetType.Crater.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.atmosphere, -1))) shouldBe false
      }
    }
  }

  "The Toxic planetType" should {

    "pass all conditions" when {

      "the attributes have a toxicity of 6" in {
        PlanetType.Toxic.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.toxicity, 6))) shouldBe true
      }
    }

    "fail all conditions" when {

      "the attributes have a toxicity greater than 6" in {
        PlanetType.Toxic.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.toxicity, 7))) shouldBe false
      }

      "the attributes have a toxicity less than 6" in {
        PlanetType.Toxic.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.toxicity, 5))) shouldBe false
      }
    }
  }

  "The Radioactive planetType" should {

    "pass all conditions" when {

      "the attributes have a radioactivity of 5" in {
        PlanetType.Radioactive.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.radioactivity, 5))) shouldBe true
      }

      "the attributes have a radioactivity of 6" in {
        PlanetType.Radioactive.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.radioactivity, 6))) shouldBe true
      }
    }

    "fail all conditions" when {

      "the attributes have a radioactivity of 4" in {
        PlanetType.Radioactive.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.radioactivity, 4))) shouldBe false
      }

      "the attributes have a radioactivity of 3" in {
        PlanetType.Radioactive.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.radioactivity, 3))) shouldBe false
      }
    }
  }

  "The Gas Giant planetType" should {

    "pass all conditions" when {

      "the attributes have a size of 7" in {
        PlanetType.GasGiant.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.size, 7))) shouldBe true
      }

      "the attributes have a size of 8" in {
        PlanetType.GasGiant.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.size, 8))) shouldBe true
      }
    }

    "fail all conditions" when {

      "the attributes have a size of 6" in {
        PlanetType.GasGiant.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.size, 6))) shouldBe false
      }

      "the attributes have a size of 5" in {
        PlanetType.GasGiant.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.size, 5))) shouldBe false
      }
    }
  }

  "The Death World planetType" should {

    "pass all conditions" when {

      "the attributes have a danger of 6" in {
        PlanetType.DeathWorld.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.danger, 6))) shouldBe true
      }
    }

    "fail all conditions" when {

      "the attributes have a danger greater than 6" in {
        PlanetType.DeathWorld.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.danger, 7))) shouldBe false
      }

      "the attributes have a danger less than 6" in {
        PlanetType.DeathWorld.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.danger, 5))) shouldBe false
      }
    }
  }

  "The Unstable planetType" should {

    "pass all conditions" when {

      "the attributes have a geology of 6" in {
        PlanetType.Unstable.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.geology, 6))) shouldBe true
      }
    }

    "fail all conditions" when {

      "the attributes have a geology greater than 6" in {
        PlanetType.Unstable.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.geology, 7))) shouldBe false
      }

      "the attributes have a geology less than 6" in {
        PlanetType.Unstable.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.geology, 5))) shouldBe false
      }
    }
  }

  "The Island planetType" should {

    "pass all conditions" when {

      "the attributes have a water of 5" in {
        PlanetType.Island.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.water, 5))) shouldBe true
      }
    }

    "fail all conditions" when {

      "the attributes have a water greater than 5" in {
        PlanetType.Island.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.water, 6))) shouldBe false
      }

      "the attributes have a water less than 5" in {
        PlanetType.Island.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.water, 4))) shouldBe false
      }
    }
  }

  "The Jungle planetType" should {

    "pass all conditions" when {

      "provided with a set of minimum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 3)
          .addOrUpdate(Attributes.temperature, 3)
          .addOrUpdate(Attributes.biosphere, 4)

        PlanetType.Jungle.conditions.forall(_(minimumAttributes)) shouldBe true
      }

      "provided with a set of maximum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 4)
          .addOrUpdate(Attributes.temperature, 6)
          .addOrUpdate(Attributes.biosphere, 6)

        PlanetType.Jungle.conditions.forall(_(minimumAttributes)) shouldBe true
      }
    }

    "fail all conditions" when {

      "provided with a set of greater than maximum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 5)
          .addOrUpdate(Attributes.temperature, 7)
          .addOrUpdate(Attributes.biosphere, 7)

        PlanetType.Jungle.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with a set of less than maximum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 2)
          .addOrUpdate(Attributes.temperature, 2)
          .addOrUpdate(Attributes.biosphere, 3)

        PlanetType.Jungle.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }
    }
  }
}
