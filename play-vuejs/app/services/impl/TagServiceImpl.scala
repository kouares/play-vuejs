package services.impl

import javax.inject.Inject

import models.Tag
import modules.AppExecutionContext
import org.joda.time.DateTime
import scalikejdbc._
import services.TagService

import scala.concurrent.Future

/**
  * Created by koichi.akimoto on 2017/07/18.
  */
class TagServiceImpl @Inject()(implicit ec: AppExecutionContext) extends TagService with SQLSyntaxSupport[Tag] {

  def findById(id: Int)(implicit session: DBSession): Future[Option[Tag]] = Future {
    Tag.find(id)
  }

  def findByName(name: String)(implicit session: DBSession): Future[Option[Tag]] = Future {
    Tag.findBy(
      sqls"""where
             name = ${name}
          """)
  }

  def findAllByName(names: Seq[String])(implicit session: DBSession): Future[Seq[Tag]] = Future {
    Tag.findAllBy(
      sqls"""where
            name in (${names})
          """
    )
  }

  def create(name: String)(implicit session: DBSession): Future[Tag] = Future {
    val current = new DateTime
    Tag.create(Some(name), None, current)
  }
}
