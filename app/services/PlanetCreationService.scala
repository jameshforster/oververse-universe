package services

import com.google.inject.{Inject, Singleton}
import models.PlanetType
import models.attributes.Attributes
import models.entities.{Entity, PlanetEntity, StarEntity}
import models.location.Coordinates
import play.api.libs.json.JsValue

import scala.collection.immutable.ListMap


@Singleton
class PlanetCreationService @Inject()(randomService: RandomService) {
  type Condition = Attributes => (Int, Int)

  val primaryAttributes: Seq[String] = Seq(Attributes.radioactivity, Attributes.geology, Attributes.minerals)

  private[services] def valueOrMin(value: Int, min: Int): Int = {
    if (value < min) min else value
  }

  private[services] def valueOrMax(value: Int, max: Int): Int = {
    if (value > max) max else value
  }

  private[services] def generatePrimaryAttributes(coordinates: Coordinates): Attributes = {

    def generateBaseAttribute(attributes: Attributes, key: String, max: Int, min: Int = 0): Attributes = {
      val result: Int = if (attributes.getAttribute[Int](Attributes.size).exists(_ < 7)) valueOrMax(randomService.generateRandomInteger(max), randomService.generateRandomInteger(max))
      else randomService.generateRandomInteger(max, min)
      attributes.addOrUpdate(key, result)
    }

    def generateSize(): Attributes = {
      if (coordinates.distanceFromOrigin() < 7) generateBaseAttribute(Attributes(Map.empty[String, JsValue]), Attributes.size, 6, 1)
      else {
        val result = generateBaseAttribute(Attributes(Map.empty[String, JsValue]), Attributes.size, 10, 1)
        result
      }
    }

    primaryAttributes.foldLeft(generateSize()) { (attributes, key) =>
      generateBaseAttribute(attributes, key, 6)
    }
  }

  private[services] val atmosphereCondition: Condition = attributes => {
    val size = attributes.getOrException[Int](Attributes.size)
    if (size > 6) (6, 6)
    else if (size < 3) (size, 0)
    else (6, 1)
  }

  private[services] def temperatureCondition(parent: StarEntity, coordinates: Coordinates): Condition = attributes => {
    val baseTemp = valueOrMin(parent.size - (coordinates.distanceFromOrigin() / 2).toInt, 0)
    val modifier = (attributes.getOrException[Int](Attributes.geology) + attributes.getOrException[Int](Attributes.atmosphere)) / 2
    (valueOrMax(baseTemp + modifier, 6), valueOrMax(baseTemp, 6))
  }

  private[services] val waterCondition: Condition = attributes => {
    val temperature = attributes.getOrException[Int](Attributes.temperature)
    val atmosphere = attributes.getOrException[Int](Attributes.atmosphere)
    if (temperature > 4 || atmosphere == 0) (0, 0)
    else (6, 0)
  }

  private[services] val biosphereCondition: Condition = attributes => {
    val water = attributes.getOrException[Int](Attributes.water)
    val radioactivity = attributes.getOrException[Int](Attributes.radioactivity)
    val geology = attributes.getOrException[Int](Attributes.geology)

    if (water > 1 && radioactivity < 5 && geology < 5) (6, 1)
    else if (water == 1 && radioactivity < 5 && geology < 5) (3, 0)
    else (0, 0)
  }

  private[services] val toxicityCondition: Condition = attributes => {
    if (attributes.getOrException[Int](Attributes.atmosphere) != 0) (6, 0)
    else (0, 0)
  }

  private[services] val breathableCondition: Condition = attributes => {
    val atmosphere = attributes.getOrException[Int](Attributes.atmosphere)
    val biosphere = attributes.getOrException[Int](Attributes.biosphere)

    if (biosphere > 0 && atmosphere > 0 && atmosphere < 6) (6, 0)
    else (0, 0)
  }

  private[services] val dangerCondition: Condition = attributes => {
    if (attributes.getOrException[Int](Attributes.breathable) > 1) (6, 0)
    else (0, 0)
  }

  def generateSecondaryAttributes(parent: StarEntity, coordinates: Coordinates): Attributes = {

    val secondaryAttributes: ListMap[String, Condition] = ListMap(
      Attributes.atmosphere -> atmosphereCondition,
      Attributes.temperature -> temperatureCondition(parent, coordinates),
      Attributes.water -> waterCondition,
      Attributes.biosphere -> biosphereCondition,
      Attributes.toxicity -> toxicityCondition,
      Attributes.breathable -> breathableCondition,
      Attributes.danger -> dangerCondition
    )

    def generateConditionalAttribute(attributes: Attributes, key: String)(f: Condition): Attributes = {
      f(attributes) match {
        case (max, min) => attributes.addOrUpdate(key, randomService.generateRandomInteger(max, min))
      }
    }

    secondaryAttributes.foldLeft(generatePrimaryAttributes(coordinates)) { (attributes, x) =>
      generateConditionalAttribute(attributes, x._1)(x._2)
    }
  }

  def generateType(attributes: Attributes): PlanetType = {
    lazy val isHabitable: Boolean = {
      PlanetType.habitableConditions.forall(_.apply(attributes))
    }

    PlanetType.extremeTypes.find(_.matchingType(attributes)).getOrElse {
      if (isHabitable) randomService.selectRandomElement(PlanetType.habitableTypes.filter(_.matchingType(attributes))).getOrElse(PlanetType.Plains)
      else randomService.selectRandomElement(PlanetType.mainTypes.filter(_.matchingType(attributes))).getOrElse(PlanetType.Barren)
    }
  }

  def createPlanet(galaxyName: String, star: StarEntity, orbitalCoordinates: Coordinates, name: String = "Unnamed World"): PlanetEntity = {
    val attributes = generateSecondaryAttributes(star, orbitalCoordinates)
    val planetType = generateType(attributes)

    PlanetEntity(randomService.generateId(), galaxyName, name, attributes.addOrUpdate(Attributes.planetType, planetType),
      star.coordinates, orbitalCoordinates, attributes.getOrException[Int](Attributes.size) * 10)
  }
}
