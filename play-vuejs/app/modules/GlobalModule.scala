package modules

import com.google.inject.AbstractModule
import services.{NotebookService, TagMstService}
import services.impl.{NotebookServiceImpl, TagMstServiceImpl}

/**
  * Created by koichi on 2017/07/17.
  */
class GrobalModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[NotebookService]).to(classOf[NotebookServiceImpl]).asEagerSingleton()
    bind(classOf[TagMstService]).to(classOf[TagMstServiceImpl]).asEagerSingleton()
  }
}
