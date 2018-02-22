package example.persistence

import com.github.tminglei.slickpg._
import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }
import example.common.config.ConfigKeeper
import example.common.domain.{ EntityStatus, UserRole }

import scala.language.higherKinds

trait DatabaseConnection extends ExPostgresProfile
  with PgDate2Support
  with PgSearchSupport
  with PgEnumSupport
  with PgPostGISSupport {

  private val config = ConfigKeeper.appConfig.database

  private val hikariConfig = new HikariConfig()

  hikariConfig.setJdbcUrl(config.jdbcUrl)
  hikariConfig.setUsername(config.username)
  hikariConfig.setPassword(config.password)
  hikariConfig.setDriverClassName(config.driver)

  val dataSource = new HikariDataSource(hikariConfig)

  override val api: API = new API {}

  val DB: backend.DatabaseDef = api.Database.forDataSource(dataSource, Some(config.maxConnections))

  trait API extends super.API with SearchApi with PostGisApi with DateApi with EnumApi {}

  trait SearchApi extends SearchImplicits with SearchAssistants { this: API ⇒

    def makeTsQuery(text: String): Rep[TsQuery] = {
      val textQuery = text.split(" ").mkString(" | ")

      toTsQuery(textQuery, Some("russian"))
    }

  }

  trait PostGisApi extends PostGISImplicits with PostGISAssistants {}

  trait DateApi extends DateTimeImplicits {}

  trait EnumApi { this: API ⇒
    implicit val userRoleMapper = createEnumJdbcType("user_role", UserRole)
    implicit val statusMapper = createEnumJdbcType("entity_status", EntityStatus)
  }

}

object DatabaseConnection extends DatabaseConnection {

  import slick.lifted.CanBeQueryCondition
  // optionally filter on a column with a supplied predicate
  case class OptionalFilter[X, Y, C[_]](query: slick.lifted.Query[X, Y, C]) {
    def filter[T, R: CanBeQueryCondition](data: Option[T])(f: T ⇒ X ⇒ R): OptionalFilter[X, Y, C] = {
      data.map(v ⇒ OptionalFilter(query.withFilter(f(v)))).getOrElse(this)
    }
  }
}
