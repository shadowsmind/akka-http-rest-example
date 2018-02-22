package example.persistence.base

import slick.dbio.Effect.Write
import slick.sql.FixedSqlAction
import example.common.domain.EntityStatus.EntityStatus
import example.common.domain.{ BaseEntityWithStatus, DateTime, EntityStatus }
import example.persistence.DatabaseConnection.DB
import example.persistence.DatabaseConnection.api._

import scala.concurrent.Future

class BaseEntityWithStatusRepository[E <: BaseEntityWithStatus, T <: BaseTableWithStatus[E]](entities: TableQuery[T])
  extends BaseEntityRepository[E, T](entities) {

  // actions
  def existsByStatusAction(id: Long, status: EntityStatus): Rep[Boolean] =
    entities.filter(e ⇒ e.id === id && e.status === status).exists

  def updateStatusAction(id: Long, status: EntityStatus, updatedAt: DateTime): FixedSqlAction[Int, NoStream, Write] =
    idFilter(id).map(e ⇒ (e.status, e.updatedAt)).update(status, Some(updatedAt))

  def updateStatusesAction(ids: Seq[Long], status: EntityStatus, updatedAt: DateTime): FixedSqlAction[Int, NoStream, Write] =
    idsFilter(ids).map(e ⇒ (e.status, e.updatedAt)).update(status, Some(updatedAt))

  // filters
  def idFilter(id: Long, status: EntityStatus): Query[T, E, Seq] =
    idFilter(id).filter(_.status === status)

  def idsFilter(ids: Seq[Long], status: EntityStatus): Query[T, E, Seq] =
    idsFilter(ids).filter(_.status === status)

  def byStatusFilter(status: EntityStatus): Query[T, E, Seq] =
    entities.filter(_.status === status)

  def afterIdFilter(id: Long, status: EntityStatus): Query[T, E, Seq] =
    afterIdFilter(id).filter(_.status === status)

  def beforeIdFilter(id: Long, status: EntityStatus): Query[T, E, Seq] =
    beforeIdFilter(id).filter(_.status === status)

  def limitFilter(limit: Int, status: EntityStatus): Query[T, E, Seq] =
    limitFilter(limit).filter(_.status === status)

  def pageableFilter(offset: Int, limit: Int, status: EntityStatus): Query[T, E, Seq] =
    pageableFilter(offset, limit).filter(_.status === status)

  def startFromIdFilter(startFromId: Long, desc: Boolean, limit: Int, status: EntityStatus): Query[T, E, Seq] = {
    val slice = startFromIdFilter(startFromId, desc, limit).filter(_.status === status)
    if (desc)
      slice.sortBy(_.id.desc)
    else
      slice
  }

  def pageableFilter(startFromId: Option[Long], desc: Boolean, limit: Int, status: EntityStatus): Query[T, E, Seq] = {
    val slice = pageableFilter(startFromId, desc, limit).filter(_.status === status)
    if (desc)
      slice.sortBy(_.id.desc)
    else
      slice
  }

  // queries
  def existsByStatus(id: Long, status: EntityStatus): Future[Boolean] =
    DB.run(existsByStatusAction(id, status).result)

  def existsActive(id: Long): Future[Boolean] =
    existsByStatus(id, EntityStatus.Active)

  def findByStatus(status: EntityStatus): Future[Seq[E]] =
    DB.run(byStatusFilter(status).result)

  def findOneByStatus(id: Long, status: EntityStatus): Future[Option[E]] =
    DB.run(idFilter(id, status).result.headOption)

  def findOneActive(id: Long): Future[Option[E]] =
    findOneByStatus(id, EntityStatus.Active)

  def findByIds(ids: Seq[Long], status: EntityStatus): Future[Seq[E]] =
    DB.run(idsFilter(ids, status).result)

  def findByIdsActive(ids: Seq[Long]): Future[Seq[E]] =
    findByIds(ids, EntityStatus.Active)

  def findAll(status: EntityStatus): Future[Seq[E]] =
    DB.run(entities.filter(_.status === status).result)

  def findAllActive: Future[Seq[E]] = findAll(EntityStatus.Active)

  def findAll(startFromId: Option[Long], desc: Boolean, limit: Int, status: EntityStatus): Future[Seq[E]] =
    DB.run(pageableFilter(startFromId, desc, limit, status).result)

  def updateStatus(id: Long, status: EntityStatus, updatedAt: DateTime): Future[Int] =
    DB.run(updateStatusAction(id, status, updatedAt).transactionally)

  def updateStatuses(ids: Seq[Long], status: EntityStatus, updatedAt: DateTime): Future[Int] =
    DB.run(updateStatusesAction(ids, status, updatedAt).transactionally)

  def delete(id: Long, updatedAt: DateTime): Future[Int] =
    updateStatus(id, EntityStatus.Deleted, updatedAt)

  def delete(ids: Seq[Long], updatedAt: DateTime): Future[Int] =
    updateStatuses(ids, EntityStatus.Deleted, updatedAt)

  def ban(id: Long, updatedAt: DateTime): Future[Int] =
    updateStatus(id, EntityStatus.Banned, updatedAt)

  def ban(ids: Seq[Long], updatedAt: DateTime): Future[Int] =
    updateStatuses(ids, EntityStatus.Banned, updatedAt)

  def enable(id: Long, updatedAt: DateTime): Future[Int] =
    updateStatus(id, EntityStatus.Active, updatedAt)

  def enable(ids: Seq[Long], updatedAt: DateTime): Future[Int] =
    updateStatuses(ids, EntityStatus.Active, updatedAt)

}
