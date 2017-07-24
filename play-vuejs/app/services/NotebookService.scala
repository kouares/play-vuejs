package services

import controllers.forms.NotebookForm.NotebookForm
import models.Notebook

import scala.concurrent.Future

/**
  * Created by koichi on 2017/07/17.
  */
trait NotebookService {

  def findById(id: Int): Future[Option[NotebookForm]]

  def findAll(): Future[Seq[NotebookForm]]

  def findAllBy(conditions: NotebookConditions): Future[Seq[NotebookForm]]

  def create(notebookForm: NotebookForm): Future[Notebook]

  def save(notebookForm: NotebookForm): Future[Notebook]

  def destroy(id: Int): Future[Int]
}

case class NotebookConditions(title: String, mainText: String)
