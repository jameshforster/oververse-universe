package models.requests

import play.api.libs.json.{Json, OFormat}

case class GenerateGalaxyRequest(name: String, size: Int, starPercentageChance: Int, cumulativeBonus: Int, planetPercentageChance: Int)

object GenerateGalaxyRequest {
  implicit val formats: OFormat[GenerateGalaxyRequest] = Json.format[GenerateGalaxyRequest]
}
