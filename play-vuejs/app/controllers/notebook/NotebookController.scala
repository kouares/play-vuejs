package controllers.notebook

import javax.inject.Inject
import javax.inject.Singleton

import controllers.forms.NotebookForm.NotebookForm
import play.api.Logger
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import play.api.libs.json._
import play.api.libs.functional.syntax._

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
class NotebookController @Inject()(cc: ControllerComponents) extends AbstractController(cc){
  private val logger = Logger(classOf[NotebookController])

  def list() = Action { implicit request: Request[AnyContent] =>
    Ok
  }

  def create() = Action { implicit request: Request[AnyContent] =>
    Ok
  }

  def update() = Action { implicit request: Request[AnyContent] =>
    Ok
  }

  def remove() = Action { implicit request: Request[AnyContent] =>
    Ok
  }
}
