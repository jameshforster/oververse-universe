package models.entities

import models.attributes.{Attributes, ColourAttribute}
import models.location.{Coordinates, ExternalLocation, Location}

case class StarEntity(galaxyName: String, name: String, attributes: Attributes, coordinates: Coordinates, signature: BigDecimal) extends Entity {
  override val entityType: String = Entity.star
  override val location: Location = ExternalLocation(galactic = coordinates, system = Coordinates(0, 0), area = Coordinates(0, 0))

  val size: Int = attributes.getOrException[Int](Attributes.size)
  val colour: ColourAttribute = ColourAttribute(attributes.getOrException[String](Attributes.colour))
  val category: String = {
    if (colour == ColourAttribute.black) "Black Hole"
    else if (colour == ColourAttribute.neutron) "Neutron Star"
    else {
      s"${colour.name} ${size match {
        case 1|2 => "Dwarf"
        case 3|4|5|6 => "Star"
        case 7|8|9 => "Giant"
        case 10 => "Supergiant"
      }}"
    }
  }
}
