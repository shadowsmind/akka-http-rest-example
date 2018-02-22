package example.persistence.base

import example.common.domain.BaseEntityWithStatus
import example.common.domain.EntityStatus.EntityStatus
import example.persistence.DatabaseConnection.api._

abstract class BaseTableWithStatus[T <: BaseEntityWithStatus](tag: Tag, tableName: String) extends BaseTable[T](tag, tableName) {

  def status: Rep[EntityStatus] = column[EntityStatus]("status")

}
