package example.persistence.migration

import org.flywaydb.core.Flyway
import example.persistence.DatabaseConnection

import scala.util.{ Failure, Success, Try }

object DatabaseSchemeMigration {

  def migrate(onFailure: () ⇒ Unit): Unit = {
    val flyway = new Flyway()
    flyway.setDataSource(DatabaseConnection.dataSource)

    Try(flyway.migrate()) match {
      case Success(c) ⇒
        println(s"Migrated $c scripts")

      case Failure(e) ⇒
        println(s"Migration failure: ${e.getMessage}")
        onFailure()
    }
  }

}
