package models.attributes

import helpers.UnitSpec
import ColourAttribute._
import models.exceptions.InvalidColourException

class ColourAttributeSpec extends UnitSpec {

  "The colour apply method" should {

    "return a matching ColourAttribute" when {
      val testMap = Map(
        "Red" -> red,
        "Blue" -> blue,
        "White" -> white,
        "Yellow" -> yellow,
        "Black" -> black,
        "Neutron" -> neutron,
        "Brown" -> brown
      )

      testMap.foreach { component =>
        val key = component._1
        val colour = component._2

        s"provided with a string of '$key'" in {
          if (validColours.forall(colour => testMap.contains(colour.name))) ColourAttribute.apply(key) shouldBe colour
          else fail("Not all valid colours included in test!")
        }
      }
    }

    "throw a InvalidColourException" when {

      "provided with a non-matching key" in {
        the[InvalidColourException] thrownBy ColourAttribute.apply("invalid") should have message "Colour: invalid is not a valid star colour!"
      }
    }
  }
}