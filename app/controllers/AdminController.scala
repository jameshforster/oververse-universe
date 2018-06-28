package controllers

import com.google.inject.{Inject, Singleton}
import models.requests.GenerateGalaxyRequest
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.GalaxyCreationService

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AdminController @Inject()(val controllerComponents: ControllerComponents, galaxyCreationService: GalaxyCreationService) extends BackendController {

  val generateGalaxy: Action[AnyContent] = {
    JsonAction.async[GenerateGalaxyRequest] {
      model =>
        val create = galaxyCreationService.saveAndCreateGalaxy(model.name, model.size, model.starPercentageChance, model.cumulativeBonus, model.planetPercentageChance)

        create.map(_ => Ok(s"The ${model.name} galaxy has been created!"))
    }
  }
}
