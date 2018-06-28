package services

import com.google.inject.Inject
import connectors.MongoConnector
import models.exceptions.MongoDatabaseException
import models.location.Coordinates
import models.{Galaxy, GalaxyModel, StarSystem}
import play.api.libs.json.{JsObject, Json}
import reactivemongo.api.commands.UpdateWriteResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GalaxyCreationService @Inject()(starSystemCreationService: StarSystemCreationService, randomService: RandomService, mongoConnector: MongoConnector) {

  def createGalaxy(name: String, size: Int, starPercentageChance: Int, cumulativeBonus: Int, planetPercentageChance: Int): Galaxy = {
    Galaxy(name, size, createStarSystems(name, size, starPercentageChance, cumulativeBonus, planetPercentageChance))
  }

  def createStarSystems(galaxyName: String, size: Int, starPercentageChance: Int, cumulativeBonus: Int, planetPercentageChance: Int): Seq[StarSystem] = {

    def applyChance(starSystems: Seq[StarSystem], x: Int, y: Int): Seq[StarSystem] = {
      val coordinates = Coordinates(x, y)
      val chance = {
        if (starSystems.forall(_.stellarObject.location.galactic.distanceFromPoint(coordinates) >= 1.5)) starPercentageChance
        else starPercentageChance + cumulativeBonus
      }

      if (chance >= randomService.generateRandomInteger(100, 0)) starSystems ++ Seq(starSystemCreationService.createSystem(galaxyName, coordinates, planetPercentageChance))
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
    for {
      stars <- mongoConnector.saveEntityArray("entities", galaxy.starSystems.map(_.stellarObject))
      majorOrbitals <- mongoConnector.saveEntityArray("entities", galaxy.starSystems.flatMap(_.majorOrbitals))
      minorOrbitals <- mongoConnector.saveEntityArray("entities", galaxy.starSystems.flatMap(_.minorOrbitals))
    } yield {
      if (stars && majorOrbitals && minorOrbitals) {
        mongoConnector.saveData("galaxies", GalaxyModel(galaxy.galaxyName, galaxy.size, active = true, test = true), JsObject(Map("galaxyName" -> Json.toJson(galaxy.galaxyName))))
      } else Future.failed(new MongoDatabaseException("Could not save all entities to database correctly!"))
    }
  }.flatten

  def saveAndCreateGalaxy(name: String, size: Int, starPercentageChance: Int, cumulativeBonus: Int, planetPercentageChance: Int): Future[UpdateWriteResult] ={
    saveGalaxy(createGalaxy(name, size, starPercentageChance, cumulativeBonus, planetPercentageChance))
  }
}
