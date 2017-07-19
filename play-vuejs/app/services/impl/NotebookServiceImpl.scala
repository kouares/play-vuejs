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
    val sortedNotes = Notebook.findAll().sortBy(note => note.title)

    val (n, tmap, tm) = (Notebook.syntax("n"), TagMapping.syntax("tmap"), TagMst.syntax("tm"))
    val notebookWithTag = sql"""
         select
           ${n.result.*}, ${tm.result.*}
         from
           ${Notebook.as(n)}
           inner join ${TagMapping.as(tmap)} on ${n.title} = ${tmap.title}
           left join ${TagMst.as(tm)} on ${tmap.tagId} = ${tm.id}
       """.map(implicit rs => (Notebook(n.resultName), TagMst(tm.resultName))).list().apply()

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

      val addedTags = Future.sequence(notebookForm.tags.map { tagName =>
        tagMstServiceImpl.findByName(tagName).map {
          case None => tagMstServiceImpl.create(tagName)
          case Some(tag) => Future {
            tag
          }
        }
      })

      addedTags.map { tags =>
        Future.sequence(tags)
      }.flatten.map { tags =>
        tags.map { tag =>
          tagMappingServiceImpl.create(notebook.title, tag.id)
        }
      }

      notebook
    }
  }

  def save(notebookForm: NotebookForm): Future[Notebook] = Future {
    Notebook.find(notebookForm.title).map { notebook =>
      notebook.copy(mainText = Some(notebookForm.mainText), upadtedAt = Some(new DateTime)).save()
    }.getOrElse(throw new RuntimeException(s"更新対象[${notebookForm.title}]の記事が存在しません"))
  }

  def destroy(notebookForm: NotebookForm): Future[Int] = Future {
    Notebook.find(notebookForm.title).map { notebook =>
      notebook.destroy()
    }.getOrElse(throw new RuntimeException(s"削除対象[${notebookForm.title}]の記事が存在しません"))
  }
}
