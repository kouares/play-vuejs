package services

import controllers.forms.NotebookForm.NotebookForm
import models.{Notebook, TagMst}

import scala.concurrent.Future

/**
  * Created by koichi.akimoto on 2017/07/18.
  */
trait TagMstService {
  def findById(id: Int): Future[Option[TagMst]]

  def findByName(name: String): Future[Option[TagMst]]

  def create(notebookForm: NotebookForm): Future[Notebook]
}
