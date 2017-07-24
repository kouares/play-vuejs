package services

import models.TagMst
import scalikejdbc.DBSession

import scala.concurrent.Future

/**
  * Created by koichi.akimoto on 2017/07/18.
  */
trait TagMstService {

  def findById(id: Int)(implicit session: DBSession): Future[Option[TagMst]]

  def findByName(name: String)(implicit session: DBSession): Future[Option[TagMst]]

  def findAllByName(names: Seq[String])(implicit session: DBSession): Future[Seq[TagMst]]

  def create(name: String)(implicit session: DBSession): Future[TagMst]
}
