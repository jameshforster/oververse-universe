package services

import com.google.inject.Inject
import models.StarSystem
import models.attributes.{Attributes, ColourAttribute}
import models.entities.{PlanetEntity, StarEntity}
import models.location.Coordinates
import play.api.libs.json.Json

class StarSystemCreationService @Inject()(randomService: RandomService, planetCreationService: PlanetCreationService) {

  def createStar(coordinates: Coordinates): StarEntity = {
    val size = randomService.generateRandomInteger(10, 1)
    val colour = size match {
      case 1 | 2 => randomService.selectRandomElement(ColourAttribute.dwarfColours).get
      case 7 | 8 | 9 => randomService.selectRandomElement(ColourAttribute.giantColours).get
      case 10 => randomService.selectRandomElement(ColourAttribute.superGiantColours).get
      case main => ColourAttribute.mainColours(main)
    }
    val attributes = Attributes(Map(
      Attributes.size -> Json.toJson(size),
      Attributes.colour -> Json.toJson(colour.name)
    ))

    StarEntity("Unnamed Star", attributes, coordinates, size * 100)
  }

  def createPlanets(star: StarEntity, percentChance: Int): Seq[PlanetEntity] = {
    def applyChance(planets: Seq[PlanetEntity], x: Int, y: Int): Seq[PlanetEntity] = {
      val coordinates = Coordinates(x, y)
      if (planets.forall(_.orbitalCoordinates.distanceFromOrigin().toInt != coordinates.distanceFromOrigin().toInt) && randomService.generateRandomInteger(100, 1) <= percentChance)
        planets ++ Seq(planetCreationService.createPlanet(star, Coordinates(x, y)))
      else
        planets
    }

    def applyChanceToAll(planets: Seq[PlanetEntity], x: Int = -8, y: Int = -8): Seq[PlanetEntity] = {
      if (x > 8) planets
      else if (y >= 8) applyChanceToAll(applyChance(planets, x, y), x + 1)
      else applyChanceToAll(applyChance(planets, x, y), x, y + 1)

    }

    applyChanceToAll(Seq())
  }

  def createSystem(coordinates: Coordinates, planetChance: Int): StarSystem = {
    val star = createStar(coordinates)
    val isPlanetSupporting = star.colour != ColourAttribute.black && star.colour != ColourAttribute.neutron
    val planets = if (isPlanetSupporting) {
      createPlanets(star, planetChance).sortWith((x, y) => x.orbitalCoordinates.distanceFromOrigin() > y.orbitalCoordinates.distanceFromOrigin())
    } else Seq()

    StarSystem(star, planets, Seq())
  }
}
