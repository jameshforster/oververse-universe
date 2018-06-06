package services

import com.google.inject.{Inject, Singleton}
import models.PlanetType
import models.attributes.Attributes
import models.entities.{Entity, PlanetEntity, StarEntity}
import models.location.{Coordinates, ExternalLocation}
import play.api.Logger
import play.api.libs.json.JsValue

import scala.collection.immutable.ListMap


@Singleton
class PlanetCreationService @Inject()(randomService: RandomService) {
  type Condition = Attributes => (Int, Int)

  val primaryAttributes: Seq[String] = Seq(Attributes.radioactivity, Attributes.geology, Attributes.minerals)

  def valueOrMin(value: Int, min: Int): Int = {
    if (value < min) min else value
  }

  def valueOrMax(value: Int, max: Int): Int = {
    if (value > max) max else value
  }

  def generatePrimaryAttributes(coordinates: Coordinates): Attributes = {

    def generateBaseAttribute(attributes: Attributes, key: String, max: Int, min: Int = 0): Attributes = {
      attributes.addOrUpdate(key, randomService.generateRandomInteger(max, min))
    }

    def generateSize(): Attributes = {
      if (coordinates.distanceFromOrigin() < 7) generateBaseAttribute(Attributes(Map.empty[String, JsValue]), Attributes.size, 6, 1)
      else generateBaseAttribute(Attributes(Map.empty[String, JsValue]), Attributes.size, 10, 1)
    }

    primaryAttributes.foldLeft(generateSize()){ (attributes, key) =>
      generateBaseAttribute(attributes, key, 6)
    }
  }

  def generateSecondaryAttributes(parent: StarEntity, coordinates: Coordinates): Attributes = {
    val atmosphereCondition: Condition = attributes => {
      val size = attributes.getOrException[Int](Attributes.size)
      if (size > 6) (6, 6)
      else (size, 0)
    }

    val temperatureCondition: Condition = attributes => {
      val baseTemp = valueOrMin(parent.size - (coordinates.distanceFromOrigin() / 2).toInt, 0)
      val modifier = (attributes.getOrException[Int](Attributes.geology) + attributes.getOrException[Int](Attributes.atmosphere))/2
      (valueOrMax(baseTemp + modifier, 6), valueOrMax(baseTemp, 6))
    }

    val waterCondition: Condition = attributes => {
      val temperature = attributes.getOrException[Int](Attributes.temperature)
      val atmosphere = attributes.getOrException[Int](Attributes.atmosphere)
      if (temperature > 4 || atmosphere == 0) (0, 0)
      else (6, 0)
    }

    val biosphereCondition: Condition = attributes => {
      val water = attributes.getOrException[Int](Attributes.water)
      val radioactivity = attributes.getOrException[Int](Attributes.radioactivity)
      val geology = attributes.getOrException[Int](Attributes.geology)

      if (water > 1 && radioactivity < 5 && geology < 5) (6, 0)
      else (0, 0)
    }

    val toxicityCondition: Condition = attributes => {
      if (attributes.getOrException[Int](Attributes.atmosphere) != 0) (6,0)
      else (0, 0)
    }

    val breathableCondition: Condition = attributes => {
      val atmosphere = attributes.getOrException[Int](Attributes.atmosphere)
      val biosphere = attributes.getOrException[Int](Attributes.biosphere)

      if (biosphere > 0 && atmosphere > 0 && atmosphere < 6) (6, 0)
      else (0, 0)
    }

    val dangerCondition: Condition = attributes => {
      if (attributes.getOrException[Int](Attributes.breathable) == 6) (6, 0)
      else (0, 0)
    }

    val secondaryAttributes: ListMap[String, Condition] = ListMap(
      Attributes.atmosphere -> atmosphereCondition,
      Attributes.temperature -> temperatureCondition,
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

    secondaryAttributes.foldLeft(generatePrimaryAttributes(coordinates)){ (attributes, x) =>
      generateConditionalAttribute(attributes, x._1)(x._2)
    }
  }

  def generateType(attributes: Attributes): PlanetType = {
    lazy val isHabitable: Boolean = {
      PlanetType.habitableConditions.forall(_.apply(attributes))
    }

    def selectValidType(types: Seq[PlanetType]) = {
      if (types.nonEmpty) {
        Some(types(randomService.generateRandomInteger(types.size - 1)))
      } else None
    }

    PlanetType.extremeTypes.find(_.matchingType(attributes)).getOrElse {
      if (isHabitable) PlanetType.habitableTypes.find(_.matchingType(attributes)).get
      else selectValidType(PlanetType.mainTypes.filter(_.matchingType(attributes))).getOrElse(PlanetType.Barren)
    }
  }

  def createPlanet(star: StarEntity, orbitalCoordinates: Coordinates, name: String = "Unnamed World"): PlanetEntity = {
    val attributes = generateSecondaryAttributes(star, orbitalCoordinates)
    val planetType = generateType(attributes)

    PlanetEntity(name, attributes.addOrUpdate(Attributes.planetType, planetType), star.coordinates, orbitalCoordinates, attributes.getOrException[Int](Attributes.size) * 10)
  }
}
