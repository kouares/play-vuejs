package services.impl

import javax.inject.Inject

import controllers.forms.NotebookForm.NotebookForm
import models.{Notebook, Tag, TagMapping}
import modules.AppExecutionContext
import org.joda.time.DateTime
import scalikejdbc._
import services.{NotebookConditions, NotebookService, TagMappingService, TagService}

import scala.concurrent.Future

/**
  * Created by koichi on 2017/07/17.
  */
class NotebookServiceImpl @Inject()(tagMstService: TagService, tagMappingService: TagMappingService)(implicit ec: AppExecutionContext) extends NotebookService with SQLSyntaxSupport[Notebook] {

  def findById(id: Int): Future[Option[NotebookForm]] = Future {
    DB readOnly { implicit session =>
      val notebook = Notebook.find(id)

      notebook.map(notebook => {
        val notebookWithTag = findNotebookWithTag(Seq(id))

        NotebookForm(notebook.id
          , notebook.title
          , notebook.mainText.getOrElse("")
          , notebookWithTag.map(nwt => nwt._2.name.getOrElse("")))
      })
    }
  }

  def findAll(): Future[Seq[NotebookForm]] = Future {
    DB readOnly { implicit session =>
      val notebooks = Notebook.findAll().sortBy(note => note.title)

      val notebookWithTag = findNotebookWithTag(notebooks.map(_.id))

      notebooks.map(notebook =>
        NotebookForm(notebook.id
          , notebook.title
          , notebook.mainText.getOrElse("")
          , notebookWithTag.filter(nwt => notebook.title == nwt._1.title).map(_._2.name.getOrElse("")))
      )
    }
  }

  def findAllBy(conditions: NotebookConditions): Future[Seq[NotebookForm]] = Future {
    DB readOnly { implicit session =>
      val notebooks = Notebook.findAllBy(
        sqls"""where
             title like '%${conditions.title}%'
             and mainText like '%${conditions.mainText}%'"""
      ).sortBy(notebook => notebook.title)

      val notebookWithTag = findNotebookWithTagBy(conditions)

      notebooks.map(notebook =>
        NotebookForm(notebook.id
          , notebook.title
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
        tags.map(_.map(tag => tagMappingService.create(notebook.id, tag.id))))

      notebook
    }
  }

  def save(notebookForm: NotebookForm): Future[Notebook] = Future {
    val current = new DateTime
    DB localTx { implicit session =>
      val notebook = Notebook.find(notebookForm.id).map { notebook =>
        notebook.copy(mainText = Some(notebookForm.mainText), updatedAt = Some(current)).save()
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
        tags.map(_.map(tag => tagMappingService.create(notebook.id, tag.id))))

      notebook
    }
  }

  def destroy(id: Int): Future[Int] = Future {
    DB localTx { implicit session =>
      val count = Notebook.find(id).map { notebook =>
        notebook.destroy()
      }.getOrElse(throw new RuntimeException(s"削除対象[${id}]の記事が存在しません"))

      val (nb, tmp) = (Notebook.syntax("nb"), TagMapping.syntax("tmp"))
      withSQL {
        select.from(Notebook as nb).
          innerJoin(TagMapping as tmp).on(nb.id, tmp.notebookId).
          where.append(sqls"""where nb.id = ${id}""")
      }.map(rs => TagMapping(tmp.resultName)(rs)).list.apply().map(TagMapping.destroy(_))

      count
    }
  }

  private def findNotebookWithTag(id: Seq[Int])(implicit session: DBSession): Seq[(Notebook, Tag)] = {
    val (nb, tmp, t) = (Notebook.syntax("nb"), TagMapping.syntax("tmp"), Tag.syntax("t"))

    withSQL {
      select.from(Notebook as nb).
        innerJoin(TagMapping as tmp).on(nb.id, tmp.notebookId).
        leftJoin(Tag as t).on(tmp.tagId, t.id).where(sqls"""where nb.id in ${id}""")
    }.map(rs => (Notebook(nb.resultName)(rs), Tag(t.resultName)(rs))).list.apply()
  }

  private def findNotebookWithTagBy(conditions: NotebookConditions)(implicit session: DBSession): Seq[(Notebook, Tag)] = {
    val (nb, tmp, t) = (Notebook.syntax("nb"), TagMapping.syntax("tmp"), Tag.syntax("t"))

    val where = (conditions.title.isEmpty, conditions.mainText.isEmpty) match {
      case (true, true) =>
        Some(sqls"""where nb.title = '%${conditions.title}%' or nb.mainText = '%${conditions.mainText}%'""")
      case (true, false) =>
        Some(sqls"""where nb.title = '%${conditions.title}%""")
      case (false, true) =>
        Some(sqls"""where nb.mainText = '${conditions.mainText}'""")
      case (false, false) => None
    }

    withSQL {
      select.from(Notebook as nb).
        innerJoin(TagMapping as tmp).on(nb.id, tmp.notebookId).
        leftJoin(Tag as t).on(tmp.tagId, t.id).where(where)
    }.map(rs => (Notebook(nb.resultName)(rs), Tag(t.resultName)(rs))).list.apply()
  }
}
