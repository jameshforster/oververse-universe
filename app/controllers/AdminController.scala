package controllers

import com.google.inject.{Inject, Singleton}
import connectors.MongoConnector
import models.requests.GenerateGalaxyRequest
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.GalaxyCreationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
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
}
