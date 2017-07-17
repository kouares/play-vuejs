package controllers.notebook

import javax.inject.Inject
import javax.inject.Singleton

import play.api.Logger
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

/**
  * Created by kouares on 2017/07/16.
  */
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
