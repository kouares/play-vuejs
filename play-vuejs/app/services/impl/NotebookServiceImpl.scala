package services.impl

import javax.inject.Inject

import controllers.forms.NotebookForm.NotebookForm
import models.{Notebook, TagMapping, TagMst}
import modules.AppExecutionContext
import services.NotebookService

import scala.concurrent.Future
import scalikejdbc._
import org.joda.time.DateTime

/**
  * Created by koichi on 2017/07/17.
  */
class NotebookServiceImpl @Inject()(tagMstServiceImpl: TagMstServiceImpl, tagMappingServiceImpl: TagMappingServiceImpl)(implicit ec: AppExecutionContext) extends NotebookService with SQLSyntaxSupport[Notebook] {

  def findByTitle(title: String): Future[Option[Notebook]] = Future {
    Notebook.find(title)
  }

  def findAll(): Future[Seq[NotebookForm]] = Future {
    DB readOnly { implicit session =>
      val sortedNotes = Notebook.findAll().sortBy(note => note.title)

      val (nb, tmp, tm) = (Notebook.syntax("nb"), TagMapping.syntax("tmp"), TagMst.syntax("tm"))
      val notebookWithTag = withSQL {
        select.from(Notebook as nb).
          innerJoin(TagMapping as tmp).on(nb.title, tmp.title).
          leftJoin(TagMst as tm).on(tmp.tagId, tm.id)
      }.map(rs => (Notebook(nb.resultName)(rs), TagMst(tm.resultName)(rs))).list.apply()

      sortedNotes.map(note =>
        NotebookForm(note.title
          , note.mainText.getOrElse("")
          , notebookWithTag.filter(nwt => note.title == nwt._1.title).map(result => result._2.name.getOrElse("")))
      )
    }
  }

  def findAllBy(notebookForm: NotebookForm): Future[Seq[Notebook]] = Future {
    Notebook.findAllBy(
      sqls"""where
             title like '%${notebookForm.title}%'
             and mainText like '%${notebookForm.mainText}%'""")
  }

  def create(notebookForm: NotebookForm): Future[Notebook] = Future {
    val current = new DateTime
    DB localTx { implicit session =>
      val notebook = Notebook.create(notebookForm.title, Some(notebookForm.mainText), Some(current), current)

      val addedTags = notebookForm.tags.map { tagName =>
        tagMstServiceImpl.findByName(tagName).map {
          case None => tagMstServiceImpl.create(tagName)
          case Some(tag) => Future { tag }
        }
      }

      Future.sequence(addedTags).map(Future.sequence(_)).map(tags =>
        tags.map(_.map(tag => tagMappingServiceImpl.create(notebook.title, tag.id))))

      notebook
    }
  }

  def save(notebookForm: NotebookForm): Future[Notebook] = Future {
    val current = new DateTime
    DB localTx { implicit session =>
      val notebook = Notebook.find(notebookForm.title).map { notebook =>
        notebook.copy(mainText = Some(notebookForm.mainText), upadtedAt = Some(current)).save()
      }.getOrElse(throw new RuntimeException(s"更新対象[${notebookForm.title}]の記事が存在しません"))

      val addedTags = notebookForm.tags.map { tagName =>
        tagMstServiceImpl.findByName(tagName).map(_ match {
          case None => tagMstServiceImpl.create(tagName)
          case Some(tag) => Future { tag}
        })
      }

      Future.sequence(addedTags).map(Future.sequence(_)).map(tags =>
        tags.map(_.map(tag => tagMappingServiceImpl.create(notebook.title, tag.id))))

      notebook
    }
  }

  def destroy(notebookForm: NotebookForm): Future[Int] = Future {
    Notebook.find(notebookForm.title).map { notebook =>
      notebook.destroy()
    }.getOrElse(throw new RuntimeException(s"削除対象[${notebookForm.title}]の記事が存在しません"))
  }
}
