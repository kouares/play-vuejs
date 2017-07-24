package modules

import com.google.inject.AbstractModule
import services.{NotebookService, TagMappingService, TagService}
import services.impl.{NotebookServiceImpl, TagMappingServiceImpl, TagServiceImpl}

/**
  * Created by koichi on 2017/07/17.
  */
class GlobalModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[NotebookService]).to(classOf[NotebookServiceImpl]).asEagerSingleton()
    bind(classOf[TagService]).to(classOf[TagServiceImpl]).asEagerSingleton()
    bind(classOf[TagMappingService]).to(classOf[TagMappingServiceImpl]).asEagerSingleton()
  }
}
