package controllers.forms

import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by koichi on 2017/07/17.
  */
object NotebookForm {
  case class NotebookForm(title: String, mainText: String, tags: Seq[String])

  val noteBookForm = Form(
    mapping(
      "title" -> nonEmptyText(maxLength = 40),
      "mainText" -> text,
      "tags" -> seq(text))(NotebookForm.apply)(NotebookForm.unapply))
}
