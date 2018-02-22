package example.common

import scala.concurrent.duration.Duration

package object config {

  case class ExampleConfig(
    server:   ServerConfig,
    database: DatabaseConfig,
    security: Security
  )

  case class ServerConfig(
    host: String,
    port: Int
  )

  case class DatabaseConfig(
    driver:         String,
    jdbcUrl:        String,
    username:       String,
    password:       String,
    maxConnections: Int
  )

  case class Security(
    tokenLifetime: Duration,
    tokenSecret:   String
  )

}
