package example.validation

import example.common.domain.accounts.Account

object NickValidator {

  def validate(nickName: String): ValidationResult[String] = {
    notEmpty(nickName, "nick_name")
  }

  def checkUnique(accountId: Long, nickName: String)(target: Option[Account]): ValidationResult[String] = {
    target match {
      case Some(account) ⇒
        if (account.id.contains(accountId)) {
          ("nick_name", "{not_changed}").invalidNel
        } else {
          ("nick_name", "{not_unique}").invalidNel
        }

      case None ⇒ nickName.validNel
    }
  }

}
