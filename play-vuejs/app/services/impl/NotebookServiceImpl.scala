package services.impl

import javax.inject.Inject

import controllers.forms.NotebookForm.NotebookForm
import models.{Notebook, TagMapping, TagMst}
import modules.AppExecutionContext
import org.joda.time.DateTime
import scalikejdbc._
import services.{NotebookService, TagMappingService, TagMstService}

import scala.concurrent.Future

/**
  * Created by koichi on 2017/07/17.
  */
class NotebookServiceImpl @Inject()(tagMstService: TagMstService, tagMappingService: TagMappingService)(implicit ec: AppExecutionContext) extends NotebookService with SQLSyntaxSupport[Notebook] {

  def findByTitle(title: String): Future[Option[NotebookForm]] = Future {
    DB readOnly { implicit session =>
      val notebook = Notebook.find(title)

      val notebookWithTag = findNotebookWithTag(Some(NotebookForm(title, "", Seq.empty[String])))

      notebook.map(note =>
        NotebookForm(note.title
          , note.mainText.getOrElse("")
          , notebookWithTag.map(nwt => nwt._2.name.getOrElse("")))
      )
    }
  }

  def findAll(): Future[Seq[NotebookForm]] = Future {
    DB readOnly { implicit session =>
      val notebooks = Notebook.findAll().sortBy(note => note.title)

      val notebookWithTag = findNotebookWithTag(None)

      notebooks.map(note =>
        NotebookForm(note.title
          , note.mainText.getOrElse("")
          , notebookWithTag.filter(nwt => note.title == nwt._1.title).map(_._2.name.getOrElse("")))
      )
    }
  }

  def findAllBy(notebookForm: NotebookForm): Future[Seq[NotebookForm]] = Future {
    DB readOnly { implicit session =>
      val notebooks = Notebook.findAllBy(
        sqls"""where
             title like '%${notebookForm.title}%'
             and mainText like '%${notebookForm.mainText}%'""").sortBy(notebook => notebook.title)

      val notebookWithTag = findNotebookWithTag(Some(notebookForm))

      notebooks.map(notebook =>
        NotebookForm(notebook.title
          , notebook.mainText.getOrElse("")
          , notebookWithTag.filter(nwt => notebook.title == nwt._1.title).map(_._2.name.getOrElse("")))
      )
    }
  }

  def create(notebookForm: NotebookForm): Future[Notebook] = Future {
    val current = new DateTime
    DB localTx { implicit session =>
      val notebook = Notebook.create(notebookForm.title, Some(notebookForm.mainText), Some(current), current)

      val addedTags = notebookForm.tags.map { tagName =>
        tagMstService.findByName(tagName).map {
          case None => tagMstService.create(tagName)
          case Some(tag) => Future {
            tag
          }
        }
      }

      Future.sequence(addedTags).map(Future.sequence(_)).map(tags =>
        tags.map(_.map(tag => tagMappingService.create(notebook.title, tag.id))))

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
        tagMstService.findByName(tagName).map(_ match {
          case None => tagMstService.create(tagName)
          case Some(tag) => Future {
            tag
          }
        })
      }

      Future.sequence(addedTags).map(Future.sequence(_)).map(tags =>
        tags.map(_.map(tag => tagMappingService.create(notebook.title, tag.id))))

      notebook
    }
  }

  def destroy(notebookForm: NotebookForm): Future[Int] = Future {
    DB localTx { implicit session =>
      val count = Notebook.find(notebookForm.title).map { notebook =>
        notebook.destroy()
      }.getOrElse(throw new RuntimeException(s"削除対象[${notebookForm.title}]の記事が存在しません"))

      val (nb, tmp) = (Notebook.syntax("nb"), TagMapping.syntax("tmp"))
      withSQL {
        select.from(Notebook as nb).
          innerJoin(TagMapping as tmp).on(nb.title, tmp.title).
          where.append(sqls"""where nb.title = ${notebookForm.title}""")
      }.map(rs => TagMapping(tmp.resultName)(rs)).list.apply().map(TagMapping.destroy(_))

      count
    }
  }

  private def findNotebookWithTag(notebookForm: Option[NotebookForm])(implicit session: DBSession): Seq[(Notebook, TagMst)] = {
    val (nb, tmp, tm) = (Notebook.syntax("nb"), TagMapping.syntax("tmp"), TagMst.syntax("tm"))

    val where = notebookForm.flatMap { condition =>
      (condition.title.isEmpty, condition.mainText.isEmpty) match {
        case (true, true) =>
          Some(sqls"""where nb.title = '%${condition.title}%' or nb.mainText = '%${condition.mainText}%'""")
        case (true, false) =>
          Some(sqls"""where nb.title = '%${condition.title}%""")
        case (false, true) =>
          Some(sqls"""where nb.mainText = '${condition.mainText}'""")
        case (false, false) => None
      }
    }

    withSQL {
      select.from(Notebook as nb).
        innerJoin(TagMapping as tmp).on(nb.title, tmp.title).
        leftJoin(TagMst as tm).on(tmp.tagId, tm.id).where(where)
    }.map(rs => (Notebook(nb.resultName)(rs), TagMst(tm.resultName)(rs))).list.apply()
  }
}
