package example.persistence.base

import java.time.LocalDateTime

import example.common.domain.{ BaseEntity, DateTime }
import example.persistence.DatabaseConnection.api._

abstract class BaseTable[T <: BaseEntity](tag: Tag, tableName: String) extends Table[T](tag, tableName) {

  // primary key
  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  // timestamps
  def createdAt: Rep[DateTime] = column[LocalDateTime]("created_at")
  def updatedAt: Rep[Option[DateTime]] = column[Option[LocalDateTime]]("updated_at")

}
