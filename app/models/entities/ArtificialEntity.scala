package models.entities

import models.attributes.{Attributes, VariableAttribute}

trait ArtificialEntity extends Entity {
  val armour: VariableAttribute = attributes.getOrException[VariableAttribute](Attributes.armour)
  val shields: BigDecimal = attributes.getOrException[BigDecimal](Attributes.shields)
  val hp: VariableAttribute = attributes.getOrException[VariableAttribute](Attributes.hp)
  val pointDefenses: BigDecimal = attributes.getOrException[BigDecimal](Attributes.pointDefenses)
  val weapons: BigDecimal = attributes.getOrException[BigDecimal](Attributes.weapons)
}
