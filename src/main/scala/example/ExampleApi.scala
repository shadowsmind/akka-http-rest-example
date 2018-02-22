package example

import akka.actor.ActorSystem
import akka.http.scaladsl.settings.ServerSettings
import akka.stream.ActorMaterializer
import com.softwaremill.macwire.wire
import example.api.WebServer
import example.api.routers.ApiRouter
import example.common.config.ConfigKeeper
import example.persistence.migration.DatabaseSchemeMigration
import example.services.{ AccountServiceImpl, AuthServiceImpl }

object ExampleApi extends App {

  val config = ConfigKeeper.appConfig

  implicit val actorSystem = ActorSystem("ExampleSystem")
  implicit val dispatcher = actorSystem.dispatcher
  implicit val materializer = ActorMaterializer()

  DatabaseSchemeMigration.migrate(onFailure = () ⇒ sys.exit)

  val accountService = wire[AccountServiceImpl]
  val authService = wire[AuthServiceImpl]

  val apiRouter = wire[ApiRouter]

  WebServer(apiRouter.routes)
    .startServer(config.server.host, config.server.port, ServerSettings(actorSystem), actorSystem)

}
