package controllers

import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import play.api.mvc.{Action, AnyContent, BaseController, Result}

import scala.concurrent.Future

trait BackendController extends BaseController {

  object JsonAction {

    def async[A](f: A => Future[Result])(implicit reads: Reads[A]): Action[AnyContent] = {
      Action.async { implicit request =>
        request.body.asJson match {
          case Some(body) => Json.fromJson[A](body) match {
            case success: JsSuccess[A] => f(success.value)
            case error: JsError => Future.successful(BadRequest(s"Invalid json errors: ${error.errors}"))
          }
          case _ => Future.successful(BadRequest("Missing json body"))
        }
      }
    }
  }
}
