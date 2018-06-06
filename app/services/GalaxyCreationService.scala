package services

import com.google.inject.Inject
import connectors.MongoConnector
import models.location.Coordinates
import models.{Galaxy, StarSystem}
import play.api.libs.json.{JsObject, Json}
import reactivemongo.api.commands.UpdateWriteResult

import scala.concurrent.Future

class GalaxyCreationService @Inject()(starSystemCreationService: StarSystemCreationService, randomService: RandomService, mongoConnector: MongoConnector) {

  def createGalaxy(name: String, size: Int, starPercentageChance: Int, cumulativeBonus: Int, planetPercentageChance: Int): Galaxy = {
    Galaxy(name, size, createStarSystems(size, starPercentageChance, cumulativeBonus, planetPercentageChance))
  }

  def createStarSystems(size: Int, starPercentageChance: Int, cumulativeBonus: Int, planetPercentageChance: Int): Seq[StarSystem] = {

    def applyChance(starSystems: Seq[StarSystem], x: Int, y: Int): Seq[StarSystem] = {
      val coordinates = Coordinates(x, y)
      val chance = {
        if (starSystems.forall(_.stellarObject.location.sector.distanceFromPoint(coordinates) >= 1.5)) starPercentageChance
        else starPercentageChance + cumulativeBonus
      }

      if (chance >= randomService.generateRandomInteger(100, 0)) starSystems ++ Seq(starSystemCreationService.createSystem(coordinates, planetPercentageChance))
      else starSystems
    }

    def applyChancesToAll(starSystems: Seq[StarSystem], x: Int, y: Int): Seq[StarSystem] = {
      if (x > size) starSystems
      else if (y >= size) applyChancesToAll(applyChance(starSystems, x, y), x + 1, y = -size)
      else applyChancesToAll(applyChance(starSystems, x, y), x, y + 1)
    }

    applyChancesToAll(Seq(), -size, -size)
  }

  def saveGalaxy(galaxy: Galaxy): Future[UpdateWriteResult] = {
    mongoConnector.saveData("galaxies", galaxy, JsObject(Map("galaxyName" -> Json.toJson(galaxy.galaxyName))))
  }

  def saveAndCreateGalaxy(name: String, size: Int, starPercentageChance: Int, cumulativeBonus: Int, planetPercentageChance: Int): Future[UpdateWriteResult] ={
    saveGalaxy(createGalaxy(name, size, starPercentageChance, cumulativeBonus, planetPercentageChance))
  }
}
