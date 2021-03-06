package services

import models.TagMapping
import scalikejdbc.DBSession

import scala.concurrent.Future

/**
  * Created by koichi on 2017/07/18.
  */
trait TagMappingService {

  private[services] def create(notebookId: Int, tagId: Int)(implicit session: DBSession): Future[TagMapping]

  private[services] def destroy(tagMapping: Seq[TagMapping])(implicit session: DBSession): Future[Seq[Int]]
}
