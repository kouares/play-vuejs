package services.impl

import java.sql.Timestamp
import javax.inject.Inject

import controllers.forms.NotebookForm.NotebookForm
import models.Notebook
import modules.AppExecutionContext
import services.NotebookService

import scala.concurrent.Future
import scalikejdbc._
import org.joda.time.DateTime

/**
  * Created by koichi on 2017/07/17.
  */
class NotebookServiceImpl @Inject()(implicit ec: AppExecutionContext) extends NotebookService with SQLSyntaxSupport[Notebook] {

  def findByTitle(title: String): Future[Option[Notebook]] = {
    Future {
      Notebook.find(title)
    }
  }

  def findAll(): Future[Seq[Notebook]] = {
    Future {
      Notebook.findAll()
    }
  }

  def findAllBy(notebookForm: NotebookForm): Future[Seq[Notebook]] = {
    Future {
      Notebook.findAllBy(
        sqls"""where
               title like '%${notebookForm.title}%'
               and mainText like '%${notebookForm.mainText}%'""")
    }
  }

  def create(notebookForm: NotebookForm): Future[Notebook] = {
    Future {
      val current = new DateTime
      Notebook.create(notebookForm.title, Some(notebookForm.mainText), Some(current), current)
    }
  }

  def save(notebookForm: NotebookForm): Future[Notebook] = {
    Future {
      Notebook.find(notebookForm.title).map { notebook =>
        notebook.copy(mainText = Some(notebookForm.mainText), upadtedAt = Some(new DateTime)).save()
      }.getOrElse(throw new RuntimeException(s"更新対象[${notebookForm.title}]の記事が存在しません"))
    }
  }

  def destroy(notebookForm: NotebookForm): Future[Int] = {
    Future {
      Notebook.find(notebookForm.title).map { notebook =>
        notebook.destroy()
      }.getOrElse(throw new RuntimeException(s"削除対象[${notebookForm.title}]の記事が存在しません"))
    }
  }
}
