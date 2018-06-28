package services

import connectors.MongoConnector
import helpers.{EntityHelper, UnitSpec}
import models.entities.Entity
import models.exceptions.MongoDatabaseException
import models.{Galaxy, StarSystem}
import models.location.Coordinates
import org.mockito.ArgumentMatchers
import org.scalatest.mockito.MockitoSugar
import reactivemongo.api.commands.UpdateWriteResult
import org.mockito.Mockito._
import play.api.libs.json.JsObject

import scala.concurrent.Future

class GalaxyCreationServiceSpec extends UnitSpec with MockitoSugar {

  val mockResult: UpdateWriteResult = mock[UpdateWriteResult]

  def setupService(randomResult: Int,
                   starSystemResult: StarSystem,
                   saveStars: Future[Boolean] = Future.successful(true),
                   saveMajorOrbitals: Future[Boolean] = Future.successful(true),
                   saveMinorOrbitals: Future[Boolean] = Future.successful(true),
                   saveGalaxy: Future[UpdateWriteResult] = Future.successful(mockResult)): GalaxyCreationService = {

    val mockMongoConnector = mock[MongoConnector]
    val mockRandomService = mock[RandomService]
    val mockSystemCreationService = mock[StarSystemCreationService]

    when(mockRandomService.generateRandomInteger(ArgumentMatchers.any[Int], ArgumentMatchers.any[Int]))
        .thenReturn(randomResult)

    when(mockSystemCreationService.createSystem(ArgumentMatchers.any[String], ArgumentMatchers.any[Coordinates], ArgumentMatchers.any[Int]))
      .thenReturn(starSystemResult)

    when(mockMongoConnector.saveEntityArray(ArgumentMatchers.any[String], ArgumentMatchers.any[Seq[Entity]])(ArgumentMatchers.any()))
      .thenReturn(saveStars, saveMajorOrbitals, saveMinorOrbitals)

    when(mockMongoConnector.saveData(ArgumentMatchers.any[String], ArgumentMatchers.any(), ArgumentMatchers.any[JsObject])(ArgumentMatchers.any()))
      .thenReturn(saveGalaxy)

    new GalaxyCreationService(mockSystemCreationService, mockRandomService, mockMongoConnector)
  }

  "Calling .createStarSystems" should {

    "return an empty array" when {

      "the chances are too low for stars to generate" in {
        val service = setupService(100, EntityHelper.testSystem)

        service.createStarSystems("galaxyName", 8, -1, 0, 15) shouldBe Seq()
      }

      "the random results prevent stars from generating" in {
        val service = setupService(5, EntityHelper.testSystem)

        service.createStarSystems("galaxyName", 4, 4, 0, 15) shouldBe Seq()
      }
    }

    "return a max size array" when {

      "the chances are too high for stars to not generate" in {
        val service = setupService(0, EntityHelper.testSystem)

        service.createStarSystems("galaxyName", 0, 100, 0, 15) shouldBe Seq(EntityHelper.testSystem)
      }

      "the random results guarantee stars generate" in {
        val service = setupService(5, EntityHelper.testSystem)
        val result = service.createStarSystems("galaxyName", 1, 5, 0, 15)

        result.size shouldBe 9
        result.forall(_.equals(EntityHelper.testSystem)) shouldBe true
      }
    }
  }

  "Calling .createGalaxy" should {

    "create a galaxy with an empty array of star systems when returned" in {
      val service = setupService(100, EntityHelper.testSystem)

      service.createGalaxy("galaxyName", 4, -1, 0, 15) shouldBe Galaxy("galaxyName", 4, Seq())
    }

    "create a galaxy with a full array of star systems when returned" in {
      val service = setupService(5, EntityHelper.testSystem)

      service.createGalaxy("newName", 0, 100, 0, 15) shouldBe Galaxy("newName", 0, Seq(EntityHelper.testSystem))
    }
  }

  "Calling .saveGalaxy" should {

    "return a WriteResult" when {

      "all mongo requests succeed" in {
        val service = setupService(100, EntityHelper.testSystem)

        await(service.saveGalaxy(Galaxy("newName", 0, Seq(EntityHelper.testSystem)))) shouldBe mockResult
      }
    }

    "return a MongoDatabaseException" when {

      "the save stars call returns a false" in {
        val service = setupService(100, EntityHelper.testSystem, saveStars = Future.successful(false))

        the[MongoDatabaseException] thrownBy await(service.saveGalaxy(Galaxy("newName", 0, Seq(EntityHelper.testSystem)))) should have message "Could not save all entities to database correctly!"
      }

      "the save major orbitals call returns a false" in {
        val service = setupService(100, EntityHelper.testSystem, saveMajorOrbitals = Future.successful(false))

        the[MongoDatabaseException] thrownBy await(service.saveGalaxy(Galaxy("newName", 0, Seq(EntityHelper.testSystem)))) should have message "Could not save all entities to database correctly!"
      }

      "the save minor orbitals call returns a false" in {
        val service = setupService(100, EntityHelper.testSystem, saveMinorOrbitals = Future.successful(false))

        the[MongoDatabaseException] thrownBy await(service.saveGalaxy(Galaxy("newName", 0, Seq(EntityHelper.testSystem)))) should have message "Could not save all entities to database correctly!"
      }
    }

    "return a generic Exception" when {

      "the save stars call returns a failure" in {
        val service = setupService(100, EntityHelper.testSystem, saveStars = Future.failed(new Exception("test error")))

        the[Exception] thrownBy await(service.saveGalaxy(Galaxy("newName", 0, Seq(EntityHelper.testSystem)))) should have message "test error"
      }

      "the save major orbitals call returns a failure" in {
        val service = setupService(100, EntityHelper.testSystem, saveMajorOrbitals = Future.failed(new Exception("test error")))

        the[Exception] thrownBy await(service.saveGalaxy(Galaxy("newName", 0, Seq(EntityHelper.testSystem)))) should have message "test error"
      }

      "the save minor orbitals call returns a failure" in {
        val service = setupService(100, EntityHelper.testSystem, saveMinorOrbitals = Future.failed(new Exception("test error")))

        the[Exception] thrownBy await(service.saveGalaxy(Galaxy("newName", 0, Seq(EntityHelper.testSystem)))) should have message "test error"
      }
    }
  }

  "Calling .saveAndCreateGalaxy" should {

    "return an UpdateWriteResult" when {

      "all calls succeed" in {
        val service = setupService(100, EntityHelper.testSystem)

        await(service.saveAndCreateGalaxy("galaxyName", 2, 50, 10, 15)) shouldBe mockResult
      }
    }

    "return an exception" when {

      "a downstream error occurs" in {
        val service = setupService(100, EntityHelper.testSystem, saveStars = Future.failed(new Exception("test error")))

        the[Exception] thrownBy await(service.saveAndCreateGalaxy("galaxyName", 2, 50, 10, 15)) should have message "test error"
      }
    }
  }
}
