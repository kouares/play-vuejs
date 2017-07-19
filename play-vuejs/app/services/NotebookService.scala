package services

import controllers.forms.NotebookForm.NotebookForm
import models.Notebook

import scala.concurrent.Future

/**
  * Created by koichi on 2017/07/17.
  */
trait NotebookService {

  def findByTitle(title: String): Future[Option[Notebook]]

  def findAll(): Future[Seq[NotebookForm]]

  def findAllBy(notebookForm: NotebookForm): Future[Seq[Notebook]]

  def create(notebookForm: NotebookForm): Future[Notebook]

  def save(notebookForm: NotebookForm): Future[Notebook]

  def destroy(notebookForm: NotebookForm): Future[Int]
}
