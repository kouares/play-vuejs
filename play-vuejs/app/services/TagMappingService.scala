package services

import models.TagMapping

import scala.concurrent.Future

/**
  * Created by koichi on 2017/07/18.
  */
trait TagMappingService {

  def create(title: String, tagId: Int): Future[TagMapping]
}
