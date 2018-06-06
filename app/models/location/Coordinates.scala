package models.location

import play.api.libs.json.{Json, OFormat}

case class Coordinates(x: Int, y: Int) {
  def distanceFromOrigin(): BigDecimal = {
    distanceFromPoint(Coordinates(0, 0))
  }

  def distanceFromPoint(destination: Coordinates): BigDecimal = {
    BigDecimal(Math.sqrt(Math.pow(x - destination.x, 2) + Math.pow(y - destination.y, 2)))
  }
}

object Coordinates {
  implicit val formats: OFormat[Coordinates] = Json.format[Coordinates]
}
