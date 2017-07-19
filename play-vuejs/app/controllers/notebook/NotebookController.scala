package controllers.notebook

import javax.inject.Inject
import javax.inject.Singleton

import controllers.forms.NotebookForm.NotebookForm
import modules.AppExecutionContext
import play.api.Logger
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.Future

/**
  * Created by kouares on 2017/07/16.
  */
object NotebookController {

  implicit val NotebookFormWrites = (
    (__ \ "title").write[String] and
    (__ \ "mainText").write[String] and
    (__ \ "tags").write[Seq[String]])(unlift(NotebookForm.unapply))

  implicit val NotebookFormReads = (
    (__ \ "title").read[String] and
    (__ \ "mainText").read[String] and
    (__ \ "tags").read[Seq[String]])(NotebookForm)
}

@Singleton
class NotebookController @Inject()(cc: ControllerComponents)(implicit ec: AppExecutionContext) extends AbstractController(cc){

  private val logger = Logger(classOf[NotebookController])

  def list() = Action.async { implicit request: Request[AnyContent] =>
    Future {
      Ok
    }
  }

  def create() = Action.async { implicit request: Request[AnyContent] =>
    Future {
      Ok
    }
  }

  def update() = Action.async { implicit request: Request[AnyContent] =>
    Future {
      Ok
    }
  }

  def remove() = Action.async { implicit request: Request[AnyContent] =>
    Future {
      Ok
    }
  }
}
