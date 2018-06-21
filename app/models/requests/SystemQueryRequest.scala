package models.requests

import models.location.Coordinates
import play.api.libs.json.{JsObject, JsValue, Json, OFormat}

case class SystemQueryRequest(galaxyName: String,
                              galacticCoordinates: Option[Coordinates] = None) extends {
  def query(): JsObject = {
    JsObject(
      Map(
        "galaxyName" -> Json.toJson(galaxyName)
      ) ++ {
        galacticCoordinates match {
          case Some(coordinates) => Map("location.galactic" -> Json.toJson(coordinates))
          case _ => Map[String, JsValue]()
        }
      }
    )
  }
}

object SystemQueryRequest {
  implicit val formats: OFormat[SystemQueryRequest] = Json.format[SystemQueryRequest]
}
