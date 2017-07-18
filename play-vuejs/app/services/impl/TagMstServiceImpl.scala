package services.impl

import javax.inject.Inject

import controllers.forms.NotebookForm.NotebookForm
import models.TagMst
import modules.AppExecutionContext
import scalikejdbc._
import services.TagMstService

import scala.concurrent.Future

/**
  * Created by koichi.akimoto on 2017/07/18.
  */
class TagMstServiceImpl @Inject()(implicit ec: AppExecutionContext) extends TagMstService with SQLSyntaxSupport[TagMst] {
  def findById(id: Int): Future[Option[TagMst]] = Future {
    TagMst.find(id)
  }

  def findByName(name: String): Future[Option[TagMst]] = Future {
    TagMst.findBy(
      sqls"""where
             name = ${name}
          """)
  }

  def create(notebookForm: NotebookForm): Future[Seq[TagMst]] = Future {
    notebookForm.tags.map { name =>
      TagMst.create(Some(name))
    }
  }
}
