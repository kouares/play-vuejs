package services.impl

import javax.inject.Inject

import models.TagMst
import modules.AppExecutionContext
import scalikejdbc._
import services.TagMstService

import scala.concurrent.Future

/**
  * Created by koichi.akimoto on 2017/07/18.
  */
class TagMstServiceImpl @Inject()(implicit ec: AppExecutionContext) extends TagMstService with SQLSyntaxSupport[TagMst] {

  def findById(id: Int)(implicit session: DBSession): Future[Option[TagMst]] = Future {
    TagMst.find(id)
  }

  def findByName(name: String)(implicit session: DBSession): Future[Option[TagMst]] = Future {
    TagMst.findBy(
      sqls"""where
             name = ${name}
          """)
  }

  def findAllByName(names: Seq[String])(implicit session: DBSession): Future[Seq[TagMst]] = Future {
    TagMst.findAllBy(
      sqls"""where
            name in (${names})
          """
    )
  }

  def create(name: String)(implicit session: DBSession): Future[TagMst] = Future {
    TagMst.create(Some(name))
  }
}
