package models.attributes

import models.exceptions.InvalidColourException

sealed trait ColourAttribute {
  val name: String
}

object ColourAttribute {
  val red: ColourAttribute = new ColourAttribute {
    override val name: String = "Red"
  }
  val blue: ColourAttribute = new ColourAttribute {
    override val name: String = "Blue"
  }
  val yellow: ColourAttribute = new ColourAttribute {
    override val name: String = "Yellow"
  }
  val white: ColourAttribute = new ColourAttribute {
    override val name: String = "White"
  }
  val black: ColourAttribute = new ColourAttribute {
    override val name: String = "Black"
  }
  val neutron: ColourAttribute = new ColourAttribute {
    override val name: String = "Neutron"
  }
  val brown: ColourAttribute = new ColourAttribute {
    override val name: String = "Brown"
  }

  private val validColours = Seq(red, blue, white, yellow, black, neutron, brown)
  val dwarfColours = Seq(white, brown)
  val mainColours = Map (3 -> red, 4 -> red, 5 -> yellow, 6 -> blue)
  val giantColours = Seq(red, yellow, red, yellow, red, yellow, red, yellow, red, yellow, neutron)
  val superGiantColours = Seq(blue, red, blue, red, blue, red, blue, red, blue, red, black)

  def apply (input: String): ColourAttribute = {
    validColours.find(_.name == input).getOrElse(throw new InvalidColourException(input))
  }
}
