package services

import models.Tag
import scalikejdbc.DBSession

import scala.concurrent.Future

/**
  * Created by koichi.akimoto on 2017/07/18.
  */
trait TagService {

  def findById(id: Int)(implicit session: DBSession): Future[Option[Tag]]

  def findByName(name: String)(implicit session: DBSession): Future[Option[Tag]]

  def findAllByName(names: Seq[String])(implicit session: DBSession): Future[Seq[Tag]]

  def create(name: String)(implicit session: DBSession): Future[Tag]
}
