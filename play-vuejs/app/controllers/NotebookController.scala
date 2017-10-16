package controllers

import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import com.google.inject.Singleton
import controllers.forms.NotebookForm._
import play.api.Logger
import play.api.i18n._
import play.api.mvc._
import services.NotebookService
import utils.FormHandleHelper

@Singleton
class NotebookController @Inject()(components: ControllerComponents, protected val notebookService: NotebookService)(implicit ec: ExecutionContext) extends AbstractController(components) with I18nSupport {

  private val logger = Logger(classOf[NotebookController])

  def index = Action {
    Ok(views.html.notebook.index())
  }

  def showcreate = Action { implicit rs: Request[_] =>

    val headers = rs.headers
    headers.keys.foreach (key => logger.info(key + " = " + headers.get(key).getOrElse("Empty")))
    logger.info("remoteAddres = " + rs.remoteAddress)

    Ok(views.html.notebook.create(noteBookForm.fill(NotebookForm("", "", Seq.empty[String]))))
  }

  def create = Action.async { implicit rs: Request[_] =>
    noteBookForm.bindFromRequest.fold(
      errors => {
        Future(BadRequest(views.html.notebook.create(errors)))
      },
      form => {
        notebookService.save(form).map(resukt => Redirect(routes.NotebookController.list))
      }
    )
  }

  def freeword = Action.async {implicit rs: Request[_] =>
    noteBookForm.bindFromRequest.fold(
      errors => {
        Future(BadRequest(views.html.notebook.list(errors, Seq.empty[NotebookForm])))
      },
      form => {
        notebookService.findAllBytitle(form).map (
          notebooks => Ok(views.html.notebook.list(noteBookForm.fill(NotebookForm("", form.mainText, Seq.empty[String])), notebooks)))
      }
    )
  }
//
//  def freeword = Action.async { implicit rs =>
//    noteBookForm.bindFromRequest.fold(
//      errors => {
//        Future {
//          BadRequest(views.html.notebook.list(errors, Seq.empty[NotebookForm]))
//        }
//      },
//      form => {
//        notebookService.listByTitle(form).map { notebooks =>
//          Ok(views.html.notebook.list(noteBookForm.fill(NotebookForm("", form.mainText, Seq.empty[String])), notebooks))
//        }
//      })
//  }
//
//  def list = Action.async { implicit rs =>
//    notebookService.list.map { result =>
//      Ok(views.html.notebook.list(noteBookForm.fill(NotebookForm("", "", Seq.empty[String])), result))
//    }
//  }
//
//  def edit(title: String) = Action.async { implicit rs =>
//    notebookService.findByTitle(title).map { x =>
//      Ok(views.html.notebook.edit(noteBookForm.fill(x)))
//    }
//  }
//
//  def update = Action.async { implicit rs =>
//    noteBookForm.bindFromRequest.fold(
//      errors => {
//        Future {
//          BadRequest(views.html.notebook.edit(errors.withError("error.not.exists.notebook", Messages("error.not.exists.notebook"), None)))
//        }
//      },
//      form => {
//        constraints.updateVerifying(form).flatMap { r =>
//          r match {
//            case Left(errorMsgCodes) => {
//              Future {
//                BadRequest(views.html.notebook.edit(FormHandleHelper.withErrors(errorMsgCodes, noteBookForm.fill(form))))
//              }
//            }
//            case Right(success) => {
//              notebookService.update(form).map(result => {
//                result match {
//                  case Right(r) => Redirect(routes.NotebookController.list)
//                  case Left(r) => BadRequest(views.html.notebook.edit(noteBookForm.fill(form).withError(r, Messages(r), None)))
//                }
//              })
//            }
//          }
//        }
//      })
//  }
//
//  def remove(title: String) = Action.async { implicit rs =>
//    notebookService.remove(title).map(result =>
//      result match {
//        case Right(r) => Redirect(routes.NotebookController.list)
//        case Left(r) => Redirect(routes.NotebookController.list)
//      })
//  }
}