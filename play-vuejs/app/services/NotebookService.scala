package services

import controllers.forms.NotebookForm.NotebookForm
import models.Notebook

import scala.concurrent.Future

/**
  * Created by koichi on 2017/07/17.
  */
trait NotebookService {

  def findByTitle(title: String): Future[Option[NotebookForm]]

  def findAll(): Future[Seq[NotebookForm]]

  def findAllBy(notebookForm: NotebookForm): Future[Seq[NotebookForm]]

  def create(notebookForm: NotebookForm): Future[Notebook]

  def save(notebookForm: NotebookForm): Future[Notebook]

  def destroy(notebookForm: NotebookForm): Future[Int]
}
