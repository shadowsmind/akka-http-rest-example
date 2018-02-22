package example.persistence

import DatabaseConnection.api._
import DatabaseConnection.DB
import slick.lifted.ProvenShape
import example.common.domain.{ DateTime, EntityStatus, UserRole }
import example.common.domain.accounts.Account
import example.persistence.base.{ BaseEntityWithStatusRepository, BaseTableWithStatus }

import scala.concurrent.Future

object AccountRepository extends BaseEntityWithStatusRepository[Account, AccountTable](TableQuery[AccountTable]) {

  def update(id: Long, nickName: String, updatedAt: DateTime): Future[Int] =
    DB.run {
      idFilter(id).map(a ⇒ (a.nickName, a.updatedAt))
        .update(nickName, Some(updatedAt))
        .transactionally
    }

  def updateEmailConfirmed(email: String): Future[Int] =
    DB.run {
      entities.filter(_.email === email).map(a ⇒ a.emailConfirmed)
        .update(true)
        .transactionally
    }

  def findByNick(nickName: String): Future[Option[Account]] =
    DB.run {
      entities.filter(a ⇒ (a.nickName === nickName) && (a.status === EntityStatus.Active))
        .result.headOption
    }

  def findByNickOrEmail(nickName: String, email: String): Future[Seq[Account]] =
    DB.run {
      entities.filter(a ⇒ (a.nickName === nickName) || (a.email === email))
        .result
    }

}

class AccountTable(tag: Tag) extends BaseTableWithStatus[Account](tag, "accounts") {

  // format: OFF
  def role           = column[UserRole]("user_role")
  def nickName       = column[String]("nick_name")
  def email          = column[String]("nick_name")
  def emailConfirmed = column[Boolean]("email_confirmed")
  def password       = column[String]("password")
  // format: ON

  override def * : ProvenShape[Account] =
    (
      id.?, createdAt, updatedAt, status,
      role, nickName, email, emailConfirmed,
      password
    ) <> (Account.tupled, Account.unapply)

}
