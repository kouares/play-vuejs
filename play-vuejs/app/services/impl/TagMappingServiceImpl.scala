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

  def create(title: String, tagId: Int): Future[TagMapping] = Future {
    TagMapping.create(title, Some(tagId))
  }
}
