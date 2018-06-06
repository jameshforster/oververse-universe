package models

import models.attributes.Attributes
import play.api.libs.json._

trait PlanetType {
  val name: String
  val conditions: Seq[Attributes => Boolean]

  def matchingType(attributes: Attributes): Boolean = {
    conditions.forall(condition => condition(attributes))
  }
}

object PlanetType {
  val extremeTypes: Seq[PlanetType] = Seq(GasGiant, Toxic, Unstable, Radioactive, DeathWorld, Crater)
  val habitableTypes: Seq[PlanetType] = Seq(Island, Gaia, Garden, Jungle, Marsh, Plains)
  val mainTypes: Seq[PlanetType] = Seq(Desert, Ocean, Arctic, Frozen, Volcanic, Magma, Mountainous, Cavernous)
  val validTypes: Seq[PlanetType] = extremeTypes ++ habitableTypes ++ mainTypes
  val habitableConditions: Seq[Attributes => Boolean] = Seq(
    attributes => attributes.getAttribute[Int](Attributes.temperature).exists(attributeBetween(4, 2)),
    attributes => attributes.getAttribute[Int](Attributes.geology).exists(attributeBetween(2, 0)),
    attributes => attributes.getAttribute[Int](Attributes.radioactivity).exists(attributeBetween(2, 0)),
    attributes => attributes.getAttribute[Int](Attributes.toxicity).exists(attributeBetween(2, 0)),
    attributes => attributes.getAttribute[Int](Attributes.atmosphere).exists(attributeBetween(4, 2)),
    attributes => attributes.getAttribute[Int](Attributes.biosphere).exists(_ > 1),
    attributes => attributes.getAttribute[Int](Attributes.breathable).exists(_ > 1)
  )

  private val writes = new Writes[PlanetType] {
    override def writes(o: PlanetType): JsValue = Json.toJson(o.name)
  }

  private val reads = new Reads[PlanetType] {
    override def reads(json: JsValue): JsResult[PlanetType] = json.validate[String].map(stringToType)
  }

  implicit val stringToType: String => PlanetType = key => validTypes.find(_.name == key).getOrElse(Barren)
  implicit val formats: Format[PlanetType] = Format(reads, writes)

  def attributeBetween(max: Int, min: Int)(attribute: Int): Boolean = {
    attribute <= max && attribute >= min
  }

  object Barren extends PlanetType {
    override val name: String = "Barren"
    override val conditions: Seq[Attributes => Boolean] = Seq()
  }

  object Crater extends PlanetType {
    override val name: String = "Crater"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.atmosphere).contains(0)
    )
  }

  object Toxic extends PlanetType {
    override val name: String = "Toxic"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.toxicity).contains(6)
    )
  }

  object Radioactive extends PlanetType {
    override val name: String = "Radioactive"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.radioactivity).exists(_ > 4)
    )
  }

  object GasGiant extends PlanetType {
    override val name: String = "Gas Giant"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.size).exists(_ > 6)
    )
  }

  object DeathWorld extends PlanetType {
    override val name: String = "Death World"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.danger).contains(6)
    )
  }

  object Unstable extends PlanetType {
    override val name: String = "Unstable"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.geology).contains(6)
    )
  }

  object Island extends PlanetType {
    override val name: String = "Island"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.water).contains(5)
    )
  }

  object Jungle extends PlanetType {
    override val name: String = "Jungle"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.water).contains(4),
      attributes => attributes.getAttribute[Int](Attributes.temperature).exists(_ > 2),
      attributes => attributes.getAttribute[Int](Attributes.biosphere).exists(_ > 4)
    )
  }

  object Marsh extends PlanetType {
    override val name: String = "Marsh"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.water).contains(4),
      attributes => attributes.getAttribute[Int](Attributes.biosphere).exists(_ > 4)
    )
  }

  object Garden extends PlanetType {
    override val name: String = "Garden"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.biosphere).exists(_ > 4),
      attributes => attributes.getAttribute[Int](Attributes.danger).exists(_ < 2)
    )
  }

  object Gaia extends PlanetType {
    override val name: String = "Gaia"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.geology).contains(2)
    )
  }

  object Plains extends PlanetType {
    override val name: String = "Plains"
    override val conditions: Seq[Attributes => Boolean] = Seq()
  }

  object Desert extends PlanetType {
    override val name: String = "Desert"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.water).contains(0)
    )
  }

  object Volcanic extends PlanetType {
    override val name: String = "Volcanic"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.temperature).exists(_ > 1),
      attributes => attributes.getAttribute[Int](Attributes.geology).exists(_ > 2),
      attributes => attributes.getAttribute[Int](Attributes.water).exists(attributeBetween(5, 1))
    )
  }

  object Magma extends PlanetType {
    override val name: String = "Magma"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.temperature).exists(_ > 4),
      attributes => attributes.getAttribute[Int](Attributes.geology).exists(_ > 2)
    )
  }

  object Arctic extends PlanetType {
    override val name: String = "Arctic"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.temperature).exists(_ < 2),
      attributes => attributes.getAttribute[Int](Attributes.water).exists(attributeBetween(5, 1))
    )
  }

  object Frozen extends PlanetType {
    override val name: String = "Frozen"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.temperature).exists(_ < 2),
      attributes => attributes.getAttribute[Int](Attributes.water).contains(6)
    )
  }

  object Ocean extends PlanetType {
    override val name: String = "Ocean"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.temperature).exists(_ > 1),
      attributes => attributes.getAttribute[Int](Attributes.water).contains(6)
    )
  }

  object Mountainous extends PlanetType {
    override val name: String = "Mountainous"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.geology).exists(_ > 2),
      attributes => attributes.getAttribute[Int](Attributes.water).exists(attributeBetween(5, 1))
    )
  }

  object Cavernous extends PlanetType {
    override val name: String = "Cavernous"
    override val conditions: Seq[Attributes => Boolean] = Seq(
      attributes => attributes.getAttribute[Int](Attributes.geology).exists(_ > 2),
      attributes => attributes.getAttribute[Int](Attributes.water).exists(attributeBetween(5, 3))
    )
  }
}