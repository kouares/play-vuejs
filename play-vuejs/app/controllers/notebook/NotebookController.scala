// TODO: JSON APIの実装
//package controllers.notebook
//
//import javax.inject.{Inject, Singleton}
//
//import controllers.forms.NotebookForm.NotebookForm
//import modules.AppExecutionContext
//import play.api.Logger
//import play.api.libs.functional.syntax._
//import play.api.libs.json._
//import play.api.mvc._
//import services.{NotebookConditions, NotebookService}
//
//import scala.concurrent.Future
//
///**
//  * Created by kouares on 2017/07/16.
//  */
//object NotebookController {
//
//  implicit val NotebookFormWrites = (
//    (__ \ "id").write[Int] and
//    (__ \ "title").write[String] and
//    (__ \ "mainText").write[String] and
//    (__ \ "tags").write[Seq[String]])(unlift(NotebookForm.unapply))
//
//  implicit val NotebookFormReads = (
//    (__ \ "id").read[Int] and
//    (__ \ "title").read[String] and
//    (__ \ "mainText").read[String] and
//    (__ \ "tags").read[Seq[String]])(NotebookForm)
//}
//
//@Singleton
//class NotebookController @Inject()(cc: ControllerComponents, notebookService: NotebookService)(implicit ec: AppExecutionContext) extends AbstractController(cc){
//  import NotebookController._
//
//  private val logger = Logger(classOf[NotebookController])
//
//  def list() = Action.async { implicit request: Request[AnyContent] =>
//    notebookService.findAll().map(serviceResult =>
//      Ok(Json.obj("notebooks" -> serviceResult.map(notebook => Json.toJson(notebook).toString)))
//    )
//  }
//
//  def search(title: String, mainText: String) = Action.async {implicit request: Request[AnyContent] =>
//    notebookService.findAllBy(NotebookConditions(title, mainText)).map(serviceResult =>
//      Ok(Json.obj("notebooks" -> serviceResult.map(notebook => Json.toJson(notebook).toString)))
//    )
//  }
//
//  def create() = Action.async(parse.json) { implicit request =>
//    val notebookFormResult = request.body.validate[NotebookForm]
//
//    notebookFormResult.fold(
//      errors => Future {
//        val jsError = JsError.toJson(errors)
//        logger.debug(jsError.toString)
//        BadRequest(Json.obj("message" -> jsError))
//      },
//      notebookForm => {
//        notebookService.create(notebookForm).map(notebook =>
//          notebookService.findById(notebook.id)
//        )flatMap(serviceResult =>
//          serviceResult.map(_ match {
//            case None => throw new RuntimeException(s"登録した記事が存在しません。タイトル[${notebookForm.title}]")
//            case Some(findNotebook) => Ok(Json.toJson(findNotebook))
//        }))
//      }
//    )
//  }
//
//  def update(id: Int) = Action.async(parse.json) { implicit request =>
//    val notebookFormResult = request.body.validate[NotebookForm]
//
//    notebookFormResult.fold(
//      errors => Future {
//        val jsError = JsError.toJson(errors)
//        logger.debug(jsError.toString)
//        BadRequest(Json.obj("message" -> jsError))
//      },
//      notebookForm => {
//        notebookService.save(notebookForm).map(notebook =>
//          notebookService.findById(notebook.id)
//        ).flatMap(serviceResult =>
//          serviceResult.map(_ match {
//            case None => InternalServerError(Json.obj("message" -> s"更新した記事が存在しません。タイトル[${notebookForm.title}]"))
//            case Some(findNotebook) => Ok(Json.toJson(findNotebook))
//        }))
//      }
//    )
//  }
//
//  def remove(id: Int) = Action.async { implicit request: Request[AnyContent] =>
//    notebookService.destroy(id).map(serviceResult =>
//      Ok(Json.obj("count" -> serviceResult))
//    )
//  }
//}
