package modules

import javax.inject.Inject

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext

/**
  * Created by koichi on 2017/07/17.
  */
class AppExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "app.executor")
