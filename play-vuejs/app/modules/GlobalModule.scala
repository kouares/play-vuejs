package modules

import com.google.inject.AbstractModule
import services.{NotebookService, TagMappingService, TagMstService}
import services.impl.{NotebookServiceImpl, TagMappingServiceImpl, TagMstServiceImpl}

/**
  * Created by koichi on 2017/07/17.
  */
class GlobalModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[NotebookService]).to(classOf[NotebookServiceImpl]).asEagerSingleton()
    bind(classOf[TagMstService]).to(classOf[TagMstServiceImpl]).asEagerSingleton()
    bind(classOf[TagMappingService]).to(classOf[TagMappingServiceImpl]).asEagerSingleton()
  }
}
