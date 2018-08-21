package controllers

import com.google.inject.Inject
import connectors.MongoConnector
import models.Galaxy
import models.requests.{PlanetQueryRequest, StarQueryRequest, SystemQueryRequest}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.{GalaxyCreationService, QueryService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class QueryUniverseController @Inject()(val controllerComponents: ControllerComponents, queryService: QueryService) extends BackendController {

  def query(queryType: String): Action[AnyContent] = {
    queryType match {
      case "planet" => planetQuery
      case "system" => systemQuery
      case "star" => starQuery
      case _ => nonMatchingQuery(queryType)
    }
  }

  val getGalaxies: Action[AnyContent] = Action.async { implicit request =>
    queryService.getGalaxies map { galaxies =>
      Ok(Json.toJson(galaxies))
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
        queryService.queryPlanets(model).map { planets =>
          Ok(Json.toJson(planets))
        }
    }
  }

  private val systemQuery: Action[AnyContent] = {
    JsonAction.async[SystemQueryRequest] {
      model =>
        queryService.querySystems(model).map { systems =>
          Ok(Json.toJson(systems))
        }
    }
  }

  private val starQuery: Action[AnyContent] = {
    JsonAction.async[StarQueryRequest] {
      model =>
        queryService.queryStars(model).map { stars =>
          Ok(Json.toJson(stars))
        }
    }
  }
}
