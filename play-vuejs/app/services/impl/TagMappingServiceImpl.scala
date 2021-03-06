package services.impl

import javax.inject.Inject

import models.TagMapping
import modules.AppExecutionContext
import scalikejdbc._
import services.TagMappingService

import scala.concurrent.Future

/**
  * Created by koichi on 2017/07/19.
  */
class TagMappingServiceImpl @Inject()(implicit ec: AppExecutionContext) extends TagMappingService with SQLSyntaxSupport[TagMapping] {

  private[services] def create(notebookId: Int, tagId: Int)(implicit session: DBSession): Future[TagMapping] = Future {
    TagMapping.create(notebookId, tagId)
  }

  private[services] def destroy(tagMappings: Seq[TagMapping])(implicit session: DBSession): Future[Seq[Int]] = Future {
    tagMappings.map(TagMapping.destroy(_))
  }
}
