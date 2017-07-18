package services

import models.TagMst

import scala.concurrent.Future

/**
  * Created by koichi.akimoto on 2017/07/18.
  */
trait TagMstService {
  def findById(id: Int): Future[Option[TagMst]]

  def findByName(name: String): Future[Option[TagMst]]

  def findAllByName(names: Seq[String]): Future[Seq[TagMst]]

  def create(name: String): Future[TagMst]
}
