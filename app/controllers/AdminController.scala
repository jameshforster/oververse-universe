package controllers

import com.google.inject.Inject
import connectors.MongoConnector
import models.Galaxy
import models.entities.Entity
import models.requests.{GenerateGalaxyRequest, PlanetQueryRequest}
import play.api.libs.json.{JsError, JsObject, JsSuccess, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.GalaxyCreationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AdminController @Inject()(val controllerComponents: ControllerComponents, galaxyCreationService: GalaxyCreationService, mongoConnector: MongoConnector) extends BaseController {

  val generateGalaxy: Action[AnyContent] = {
    Action.async { implicit request =>
      request.body.asJson match {
        case Some(bodyVal) => Json.fromJson[GenerateGalaxyRequest](bodyVal) match {
          case success: JsSuccess[GenerateGalaxyRequest] => {
            val model = success.value
            val create = galaxyCreationService.saveAndCreateGalaxy(model.name, model.size, model.starPercentageChance, model.cumulativeBonus, model.planetPercentageChance)

            create.map(_ => Ok(s"The ${model.name} galaxy has been created!"))
          }
          case error: JsError => Future.successful(BadRequest(s"Invalid json errors: ${error.errors}"))
        }
        case _ => Future.successful(BadRequest("Missing json body"))
      }
    }
  }

  val planetQuery: Action[AnyContent] = {
    Action.async { implicit request =>
      request.body.asJson match {
        case Some(bodyVal) => Json.fromJson[PlanetQueryRequest](bodyVal) match {
          case success: JsSuccess[PlanetQueryRequest] => {
            val model = success.value

            mongoConnector.findData[Galaxy]("galaxies", JsObject(Map("galaxyName" -> Json.toJson(model.galaxyName)))).map {
              case Some(galaxy) => Ok(Json.toJson(model.query(galaxy)))
              case _ => NotFound("Invalid galaxy name!")
            }
          }
          case error: JsError => Future.successful(BadRequest(s"Invalid json errors: ${error.errors}"))
        }
        case _ => Future.successful(BadRequest("Missing json body"))
      }
    }
  }
}
