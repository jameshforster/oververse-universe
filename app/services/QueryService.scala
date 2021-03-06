package services

import com.google.inject.Inject
import connectors.MongoConnector
import models.entities.{Entity, PlanetEntity, StarEntity, StationEntity}
import models.requests.{PlanetQueryRequest, StarQueryRequest, SystemQueryRequest}
import models.{GalaxyModel, StarSystem}
import play.api.libs.json.JsObject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class QueryService @Inject()(mongoConnector: MongoConnector) {

  def queryPlanets(planetQueryRequest: PlanetQueryRequest): Future[Seq[PlanetEntity]] = {
    mongoConnector.findAllData[Entity]("entities", planetQueryRequest.query()).map(
      _.flatMap {
        case e: PlanetEntity => Some(e)
        case _ => None
      }
    )
  }

  def queryStars(starQueryRequest: StarQueryRequest): Future[Seq[StarEntity]] = {
    mongoConnector.findAllData[Entity]("entities", starQueryRequest.query()).map(
      _.flatMap {
        case e: StarEntity => Some(e)
        case _ => None
      }
    )
  }

  def querySystems(systemQueryRequest: SystemQueryRequest): Future[Seq[StarSystem]] = {

    def getStars(entities: Seq[Entity]): Seq[StarEntity] = {
      entities.flatMap {
        case e: StarEntity => Some(e)
        case _ => None
      }
    }

    def getSystems(entities: Seq[Entity]): Seq[StarSystem] = {
      getStars(entities).map { star =>
        val matchingEntities = entities.filter(entity => entity.location.galactic == star.location.galactic && !entity.isInstanceOf[StarEntity])
        StarSystem(star,
          matchingEntities.filter(_.isInstanceOf[PlanetEntity]),
          matchingEntities.filter(_.isInstanceOf[StationEntity]),
          matchingEntities.filterNot(entity => entity.isInstanceOf[PlanetEntity] || entity.isInstanceOf[StationEntity]))
      }
    }

    mongoConnector.findAllData[Entity]("entities", systemQueryRequest.query()).map(getSystems)
  }

  def getGalaxies: Future[Seq[GalaxyModel]] = {
    mongoConnector.findAllData[GalaxyModel]("galaxies", JsObject(Seq.empty))
  }
}
