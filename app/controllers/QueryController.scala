package controllers

import com.google.inject.Inject
import connectors.MongoConnector
import models.Galaxy
import models.requests.{PlanetQueryRequest, StarQueryRequest, SystemQueryRequest}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.GalaxyCreationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class QueryController @Inject()(val controllerComponents: ControllerComponents, galaxyCreationService: GalaxyCreationService, mongoConnector: MongoConnector) extends BackendController {

  def query(queryType: String): Action[AnyContent] = {
    queryType match {
      case "planet" => planetQuery
      case "system" => systemQuery
      case "star" => starQuery
      case _ => nonMatchingQuery(queryType)
    }
  }

  private def nonMatchingQuery(nonMatchingType: String): Action[AnyContent] = {
    Action.async { implicit request =>
      Future.successful(BadRequest(s"Invalid query type: $nonMatchingType"))
    }
  }

  private val planetQuery: Action[AnyContent] = {
    JsonAction.async[PlanetQueryRequest] {
      model =>
        mongoConnector.findData[Galaxy]("galaxies", JsObject(Map("galaxyName" -> Json.toJson(model.galaxyName)))).map {
          case Some(galaxy) => Ok(Json.toJson(model.query(galaxy)))
          case _ => NotFound("Invalid galaxy name!")
        }
    }
  }

  private val systemQuery: Action[AnyContent] = {
    JsonAction.async[SystemQueryRequest] {
      model =>
        mongoConnector.findData[Galaxy]("galaxies", JsObject(Map("galaxyName" -> Json.toJson(model.galaxyName)))).map {
          case Some(galaxy) => Ok(Json.toJson(model.query(galaxy)))
          case _ => NotFound("Invalid galaxy name!")
        }
    }
  }

  private val starQuery: Action[AnyContent] = {
    JsonAction.async[StarQueryRequest] {
      model =>
        mongoConnector.findData[Galaxy]("galaxies", JsObject(Map("galaxyName" -> Json.toJson(model.galaxyName)))).map {
          case Some(galaxy) => Ok(Json.toJson(model.query(galaxy)))
          case _ => NotFound("Invalid galaxy name!")
        }
    }
  }
}
