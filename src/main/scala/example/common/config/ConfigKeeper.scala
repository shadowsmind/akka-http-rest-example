package example.common.config

import com.typesafe.config.{ Config, ConfigFactory }
import pureconfig.loadConfigOrThrow

object ConfigKeeper {

  val config: Config = ConfigFactory.load()

  val appConfig: ExampleConfig = loadConfigOrThrow[ExampleConfig](config)

}
