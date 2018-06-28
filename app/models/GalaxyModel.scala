package models

import play.api.libs.json.{Json, OFormat}

case class GalaxyModel(galaxyName: String, size: Int, active: Boolean, test: Boolean)

object GalaxyModel {
  implicit val formats: OFormat[GalaxyModel] = Json.format[GalaxyModel]
}
