package example.persistence.base

import slick.dbio.Effect.Write
import slick.sql.{ FixedSqlAction, SqlAction }
import example.common.domain.BaseEntity
import example.persistence.DatabaseConnection.DB
import example.persistence.DatabaseConnection.api._

import scala.concurrent.Future

class BaseEntityRepository[E <: BaseEntity, T <: BaseTable[E]](val entities: TableQuery[T]) {

  val tableName: String = entities.baseTableRow.tableName

  private[persistence] def createSchema: Future[Unit] =
    DB.run(entities.schema.create)

  // compiled functions
  val byId = entities.findBy(_.id)

  // actions
  def existsAction(id: Long): Rep[Boolean] =
    entities.filter(_.id === id).exists

  def saveAction(entity: E): FixedSqlAction[E, NoStream, Write] =
    entities returning entities += entity

  def saveAllAction(entitiesList: Seq[E]): FixedSqlAction[Seq[E], NoStream, Write] =
    entities returning entities ++= entitiesList

  def updateAction(id: Long, entity: E): FixedSqlAction[Int, NoStream, Write] =
    idFilter(id).update(entity)

  def incrementAction(id: Long, column: String): SqlAction[Int, NoStream, Effect] =
    sqlu"""update #$tableName set #$column = #$column + 1 where id = $id"""

  def decrementAction(id: Long, column: String): SqlAction[Int, NoStream, Effect] =
    sqlu"""update #$tableName set #$column = #$column - 1 where id = $id"""

  // filters
  def idFilter(id: Long): Query[T, E, Seq] =
    entities.filter(_.id === id)

  def idsFilter(ids: Seq[Long]): Query[T, E, Seq] =
    entities.filter(_.id inSet ids)

  def afterIdFilter(id: Long): Query[T, E, Seq] =
    entities.filter(_.id > id)

  def beforeIdFilter(id: Long): Query[T, E, Seq] =
    entities.filter(_.id < id)

  def limitFilter(limit: Int): Query[T, E, Seq] =
    entities.take(limit)

  def startFromIdFilter(startFromId: Long, desc: Boolean, limit: Int): Query[T, E, Seq] = {
    val slice = if (desc) {
      beforeIdFilter(startFromId)
    } else {
      afterIdFilter(startFromId)
    }

    slice.take(limit)
  }

  def pageableFilter(startFromId: Option[Long], desc: Boolean, limit: Int): Query[T, E, Seq] = {
    startFromId match {
      case Some(id) ⇒ startFromIdFilter(id, desc, limit)
      case None     ⇒ limitFilter(limit)
    }
  }

  def pageableFilter(offset: Int, limit: Int): Query[T, E, Seq] =
    entities.drop(offset).take(limit)

  // queries
  def exists(id: Long): Future[Boolean] =
    DB.run(existsAction(id).result)

  def save(entity: E): Future[E] =
    DB.run(saveAction(entity).transactionally)

  def saveAll(entities: Seq[E]): Future[Seq[E]] =
    DB.run(saveAllAction(entities).transactionally)

  def update(id: Long, entity: E): Future[Int] =
    DB.run(updateAction(id, entity).transactionally)

  def increment(id: Long, column: String): Future[Int] =
    DB.run(incrementAction(id, column).transactionally)

  def decrement(id: Long, column: String): Future[Int] =
    DB.run(decrementAction(id, column).transactionally)

  def findOne(id: Long): Future[Option[E]] =
    DB.run(byId(id).result.headOption)

  def findByIds(ids: Seq[Long]): Future[Seq[E]] =
    DB.run(idsFilter(ids).result)

  def findAll: Future[Seq[E]] =
    DB.run(entities.result)

  def findAll(startFromId: Option[Long], desc: Boolean, limit: Int): Future[Seq[E]] =
    DB.run(pageableFilter(startFromId, desc, limit).result)

}
