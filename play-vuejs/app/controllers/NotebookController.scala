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
import services.NotebookConditions

@Singleton
class NotebookController @Inject()(components: ControllerComponents, notebookService: NotebookService)(implicit ec: ExecutionContext) extends AbstractController(components) with I18nSupport {

  private val logger = Logger(classOf[NotebookController])

  def index = Action {
    Ok(views.html.notebook.index())
  }

  def showcreate = Action { implicit rs: Request[_] =>

    val headers = rs.headers
    headers.keys.foreach (key => logger.info(key + " = " + headers.get(key).getOrElse("Empty")))
    logger.info("remoteAddres = " + rs.remoteAddress)

    Ok(views.html.notebook.create(noteBookForm.fill(NotebookForm(None, "", "", Seq.empty[String]))))
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

  def freeword = Action.async { implicit rs =>
    noteBookForm.bindFromRequest.fold(
      errors => {
        Future(BadRequest(views.html.notebook.list(errors, Seq.empty[NotebookForm])))
      },
      form => {
        notebookService.findAllBy(NotebookConditions(form.title, form.mainText)).map (
          notebooks => Ok(views.html.notebook.list(noteBookForm.fill(NotebookForm(None, "", form.mainText, Seq.empty[String])), notebooks)))
      }
    )
  }

  def list = Action.async { implicit rs =>
    notebookService.findAll.map { result =>
      Ok(views.html.notebook.list(noteBookForm.fill(NotebookForm(None, "", "", Seq.empty[String])), result))
    }
  }

  def edit(id: Int) = Action.async { implicit rs =>
    notebookService.findById(id).map { notebook =>
      Ok(views.html.notebook.edit(noteBookForm.fill(notebook.getOrElse(throw new RuntimeException("idに紐づくNotebookが存在しません")))))
    }
  }

  def update = Action.async { implicit rs =>
    noteBookForm.bindFromRequest.fold(
      errors => {
        Future {
          BadRequest(views.html.notebook.edit(errors.withError("error.not.exists.notebook", Messages("error.not.exists.notebook"), None)))
        }
      },
      form => {
        notebookService.save(form).map(_ => Redirect(routes.NotebookController.list))
      })
  }

  def remove(id: Int) = Action.async { implicit rs =>
    notebookService.destroy(id).map(result => Redirect(routes.NotebookController.list))
  }
}