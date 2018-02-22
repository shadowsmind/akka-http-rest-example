package example.services

import example.common.config.ConfigKeeper
import example.common.domain.accounts.Account
import example.common.domain.auth._
import example.persistence.AccountRepository
import example.common.utils.{ JwtHelper, SecurityHelper }

import scala.concurrent.ExecutionContextExecutor

trait AuthService {

  private val config = ConfigKeeper.appConfig.security

  def auth(dto: AuthViaPasswordDto): AsyncServiceResult[AuthToken]

  def encodeToken(account: Account): AuthToken = {
    import AuthJsonProtocol._
    val session = UserSession.fromAccount(account, config.tokenLifetime.toDays)
    val content = toJson(session).compactPrint
    val token = JwtHelper.encode(content, config.tokenSecret)

    AuthToken(token, session)
  }

}

class AuthServiceImpl(implicit ex: ExecutionContextExecutor) extends AuthService {

  // TODO: check login is email or nickName and after find
  def auth(dto: AuthViaPasswordDto): AsyncServiceResult[AuthToken] = {
    AccountRepository.findByNick(dto.login).flatMap {
      case Some(account) ⇒
        val isSamePassword = SecurityHelper.passwordMatch(dto.password, account.password)

        if (isSamePassword) {
          ServiceResult.result(encodeToken(account))
        } else {
          ServiceResult.error(403, "{login_or_pass_wrong}")
        }

      case None ⇒
        ServiceResult.error(403, "{login_or_pass_wrong}")
    }
  }

}
