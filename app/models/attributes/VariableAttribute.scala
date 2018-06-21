package models.attributes

import play.api.libs.json.{Json, OFormat}

case class VariableAttribute(max: BigDecimal, current: BigDecimal)

object VariableAttribute {
  implicit val formats: OFormat[VariableAttribute] = Json.format[VariableAttribute]
}
