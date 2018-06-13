package models

import models.entities.Entity
import play.api.libs.json.{Json, OFormat}

case class StarSystem(stellarObject: Entity, majorOrbitals: Seq[Entity], minorOrbitals: Seq[Entity], otherEntities: Seq[Entity])

object StarSystem {
  implicit val formats: OFormat[StarSystem] = Json.format[StarSystem]
}
