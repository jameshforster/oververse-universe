package models

import play.api.libs.json.{Json, OFormat}

case class Galaxy(galaxyName: String, size: Int, starSystems: Seq[StarSystem])

object Galaxy {
  implicit val formats: OFormat[Galaxy] = Json.format[Galaxy]
}