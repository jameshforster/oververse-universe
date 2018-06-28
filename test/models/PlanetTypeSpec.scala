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

      "provided with no attributes" in {
        PlanetType.Crater.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
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

      "provided with no attributes" in {
        PlanetType.Toxic.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
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

      "provided with no attributes" in {
        PlanetType.Radioactive.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
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

      "provided with no attributes" in {
        PlanetType.GasGiant.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
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

      "provided with no attributes" in {
        PlanetType.DeathWorld.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
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

      "provided with no attributes" in {
        PlanetType.Unstable.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
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

      "provided with no attributes" in {
        PlanetType.Island.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
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

        PlanetType.Jungle.conditions.forall(_ (minimumAttributes)) shouldBe true
      }

      "provided with a set of maximum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 4)
          .addOrUpdate(Attributes.temperature, 6)
          .addOrUpdate(Attributes.biosphere, 6)

        PlanetType.Jungle.conditions.forall(_ (minimumAttributes)) shouldBe true
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

      "provided with a set of less than minimum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 2)
          .addOrUpdate(Attributes.temperature, 2)
          .addOrUpdate(Attributes.biosphere, 3)

        PlanetType.Jungle.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with no attributes" in {
        PlanetType.Jungle.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
      }
    }
  }

  "The Marsh planetType" should {

    "pass all conditions" when {

      "provided with a set of minimum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 3)
          .addOrUpdate(Attributes.biosphere, 4)

        PlanetType.Marsh.conditions.forall(_ (minimumAttributes)) shouldBe true
      }

      "provided with a set of maximum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 4)
          .addOrUpdate(Attributes.biosphere, 6)

        PlanetType.Marsh.conditions.forall(_ (minimumAttributes)) shouldBe true
      }
    }

    "fail all conditions" when {

      "provided with a set of greater than maximum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 5)
          .addOrUpdate(Attributes.biosphere, 7)

        PlanetType.Marsh.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with a set of less than minimum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 2)
          .addOrUpdate(Attributes.biosphere, 3)

        PlanetType.Marsh.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with no attributes" in {
        PlanetType.Marsh.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
      }
    }
  }

  "The Garden planetType" should {

    "pass all conditions" when {

      "provided with a set of values within the limit" in {
        val withinAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 3)
          .addOrUpdate(Attributes.danger, 0)
          .addOrUpdate(Attributes.biosphere, 5)
          .addOrUpdate(Attributes.geology, 0)

        PlanetType.Garden.conditions.forall(_ (withinAttributes)) shouldBe true
      }

      "provided with a set of values equal to the limit" in {
        val limitAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 4)
          .addOrUpdate(Attributes.danger, 1)
          .addOrUpdate(Attributes.biosphere, 4)
          .addOrUpdate(Attributes.geology, 1)

        PlanetType.Garden.conditions.forall(_ (limitAttributes)) shouldBe true
      }
    }

    "fail all conditions" when {

      "provided with a set of values beyond the limit" in {
        val beyondLimitAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 5)
          .addOrUpdate(Attributes.danger, 2)
          .addOrUpdate(Attributes.biosphere, 3)
          .addOrUpdate(Attributes.geology, 2)

        PlanetType.Garden.conditions.forall(condition => !condition(beyondLimitAttributes)) shouldBe true
      }

      "provided with no attributes" in {
        PlanetType.Garden.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
      }
    }
  }

  "The Gaia planetType" should {

    "pass all conditions" when {

      "provided with a set of minimum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 0)
          .addOrUpdate(Attributes.geology, 2)

        PlanetType.Gaia.conditions.forall(_ (minimumAttributes)) shouldBe true
      }

      "provided with a set of maximum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 4)
          .addOrUpdate(Attributes.geology, 3)

        PlanetType.Gaia.conditions.forall(_ (minimumAttributes)) shouldBe true
      }
    }

    "fail all conditions" when {

      "provided with a set of greater than maximum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 5)
          .addOrUpdate(Attributes.geology, 4)

        PlanetType.Gaia.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with a set of less than minimum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, -1)
          .addOrUpdate(Attributes.geology, 1)

        PlanetType.Gaia.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with no attributes" in {
        PlanetType.Gaia.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
      }
    }
  }

  "The Plains planetType" should {

    "have no conditions" in {
      PlanetType.Plains.conditions.size shouldBe 0
    }
  }

  "The Desert planetType" should {

    "pass all conditions" when {

      "the attributes have a water of 0" in {
        PlanetType.Desert.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.water, 0))) shouldBe true
      }
    }

    "fail all conditions" when {

      "the attributes have a water greater than 0" in {
        PlanetType.Desert.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.water, 1))) shouldBe false
      }

      "the attributes have a water less than 0" in {
        PlanetType.Desert.conditions.forall(_ (Attributes.emptyAttributes.addOrUpdate(Attributes.water, -1))) shouldBe false
      }

      "provided with no attributes" in {
        PlanetType.Desert.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
      }
    }
  }

  "The Volcanic planetType" should {

    "pass all conditions" when {

      "provided with a set of minimum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 1)
          .addOrUpdate(Attributes.geology, 3)
          .addOrUpdate(Attributes.temperature, 2)

        PlanetType.Volcanic.conditions.forall(_ (minimumAttributes)) shouldBe true
      }

      "provided with a set of maximum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 5)
          .addOrUpdate(Attributes.geology, 6)
          .addOrUpdate(Attributes.temperature, 6)

        PlanetType.Volcanic.conditions.forall(_ (minimumAttributes)) shouldBe true
      }
    }

    "fail all conditions" when {

      "provided with a set of greater than maximum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 6)
          .addOrUpdate(Attributes.geology, 7)
          .addOrUpdate(Attributes.temperature, 7)

        PlanetType.Volcanic.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with a set of less than minimum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 0)
          .addOrUpdate(Attributes.geology, 2)
          .addOrUpdate(Attributes.temperature, 1)

        PlanetType.Volcanic.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with no attributes" in {
        PlanetType.Volcanic.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
      }
    }
  }

  "The Magma planetType" should {

    "pass all conditions" when {

      "provided with a set of minimum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.geology, 3)
          .addOrUpdate(Attributes.temperature, 5)

        PlanetType.Magma.conditions.forall(_ (minimumAttributes)) shouldBe true
      }

      "provided with a set of greater than minimum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.geology, 4)
          .addOrUpdate(Attributes.temperature, 6)

        PlanetType.Magma.conditions.forall(_ (minimumAttributes)) shouldBe true
      }
    }

    "fail all conditions" when {

      "provided with a set of less than minimum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.geology, 2)
          .addOrUpdate(Attributes.temperature, 4)

        PlanetType.Magma.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with no attributes" in {
        PlanetType.Magma.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
      }
    }
  }

  "The Arctic planetType" should {

    "pass all conditions" when {

      "provided with a set of minimum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 1)
          .addOrUpdate(Attributes.temperature, 0)

        PlanetType.Arctic.conditions.forall(_ (minimumAttributes)) shouldBe true
      }

      "provided with a set of maximum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 5)
          .addOrUpdate(Attributes.temperature, 1)

        PlanetType.Arctic.conditions.forall(_ (minimumAttributes)) shouldBe true
      }
    }

    "fail all conditions" when {

      "provided with a set of greater than maximum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 6)
          .addOrUpdate(Attributes.temperature, 2)

        PlanetType.Arctic.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with a set of less than minimum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 0)
          .addOrUpdate(Attributes.temperature, -1)

        PlanetType.Arctic.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with no attributes" in {
        PlanetType.Arctic.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
      }
    }
  }

  "The Frozen planetType" should {

    "pass all conditions" when {

      "provided with a set of minimum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 6)
          .addOrUpdate(Attributes.temperature, 0)

        PlanetType.Frozen.conditions.forall(_ (minimumAttributes)) shouldBe true
      }

      "provided with a set of maximum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 6)
          .addOrUpdate(Attributes.temperature, 1)

        PlanetType.Frozen.conditions.forall(_ (minimumAttributes)) shouldBe true
      }
    }

    "fail all conditions" when {

      "provided with a set of greater than maximum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 7)
          .addOrUpdate(Attributes.temperature, 2)

        PlanetType.Frozen.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with a set of less than minimum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 5)
          .addOrUpdate(Attributes.temperature, -1)

        PlanetType.Frozen.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with no attributes" in {
        PlanetType.Frozen.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
      }
    }
  }

  "The Ocean planetType" should {

    "pass all conditions" when {

      "provided with a set of minimum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 6)
          .addOrUpdate(Attributes.temperature, 2)

        PlanetType.Ocean.conditions.forall(_ (minimumAttributes)) shouldBe true
      }

      "provided with a set of maximum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 6)
          .addOrUpdate(Attributes.temperature, 6)

        PlanetType.Ocean.conditions.forall(_ (minimumAttributes)) shouldBe true
      }
    }

    "fail all conditions" when {

      "provided with a set of greater than maximum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 7)
          .addOrUpdate(Attributes.temperature, 7)

        PlanetType.Ocean.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with a set of less than minimum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 5)
          .addOrUpdate(Attributes.temperature, 1)

        PlanetType.Ocean.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with no attributes" in {
        PlanetType.Ocean.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
      }
    }
  }

  "The Mountainous planetType" should {

    "pass all conditions" when {

      "provided with a set of minimum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 1)
          .addOrUpdate(Attributes.geology, 3)

        PlanetType.Mountainous.conditions.forall(_ (minimumAttributes)) shouldBe true
      }

      "provided with a set of maximum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 5)
          .addOrUpdate(Attributes.geology, 6)

        PlanetType.Mountainous.conditions.forall(_ (minimumAttributes)) shouldBe true
      }
    }

    "fail all conditions" when {

      "provided with a set of greater than maximum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 6)
          .addOrUpdate(Attributes.geology, 7)

        PlanetType.Mountainous.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with a set of less than minimum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 0)
          .addOrUpdate(Attributes.geology, 2)

        PlanetType.Mountainous.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with no attributes" in {
        PlanetType.Mountainous.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
      }
    }
  }

  "The Cavernous planetType" should {

    "pass all conditions" when {

      "provided with a set of minimum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 3)
          .addOrUpdate(Attributes.geology, 3)

        PlanetType.Cavernous.conditions.forall(_ (minimumAttributes)) shouldBe true
      }

      "provided with a set of maximum valid attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 5)
          .addOrUpdate(Attributes.geology, 6)

        PlanetType.Cavernous.conditions.forall(_ (minimumAttributes)) shouldBe true
      }
    }

    "fail all conditions" when {

      "provided with a set of greater than maximum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 6)
          .addOrUpdate(Attributes.geology, 7)

        PlanetType.Cavernous.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with a set of less than minimum attributes" in {
        val minimumAttributes = Attributes.emptyAttributes
          .addOrUpdate(Attributes.water, 2)
          .addOrUpdate(Attributes.geology, 2)

        PlanetType.Cavernous.conditions.forall(condition => !condition(minimumAttributes)) shouldBe true
      }

      "provided with no attributes" in {
        PlanetType.Cavernous.conditions.forall(condition => !condition(Attributes.emptyAttributes)) shouldBe true
      }
    }
  }
}
