package example.common.domain

import example.api.protocol.ApiJsonProtocol
import example.common.domain.accounts.Account
import example.common.utils.DateHelper

object auth {

  case class UserSession(
    accountId: Long,
    role:      UserRole = UserRole.User,
    createdAt: Long     = DateHelper.nowTimestamp,
    expireAt:  Long     = DateHelper.nowTimestamp + (30 * 60 * 60 * 1000)
  ) extends HasRole {

    def hasAccess(id: Long): Boolean = (id == accountId) || isAdmin

  }

  object UserSession {

    def fromAccount(account: Account, lifeTimeDays: Long): UserSession = {
      val timestamp = DateHelper.nowTimestamp
      val offset = lifeTimeDays * 24 * 60 * 60 * 1000

      UserSession(
        accountId = account.id.get,
        role      = account.role,
        createdAt = timestamp,
        expireAt  = timestamp + offset
      )
    }

  }

  case class AuthToken(token: String, session: UserSession)

  case class AuthTokenDto(role: UserRole, token: String, createdAt: Long, expiryAt: Long)

  case class AuthViaPasswordDto(login: String, password: String)

  object AuthTokenDto {

    def fromToken(authToken: AuthToken): AuthTokenDto = {
      AuthTokenDto(
        role      = authToken.session.role,
        token     = authToken.token,
        createdAt = authToken.session.createdAt,
        expiryAt  = authToken.session.expireAt
      )
    }

  }

  trait AuthJsonProtocol extends ApiJsonProtocol {

    implicit val authViaPasswordFormat = jsonFormat2(AuthViaPasswordDto)
    implicit val userSessionFormat = jsonFormat(UserSession.apply, "account_id", "role", "created_at", "expiry_at")
    implicit val authTokenFormat = jsonFormat(AuthTokenDto.apply, "role", "token", "created_at", "expiry_at")
    implicit val authTokenResponseFormat = apiResponseFormat[AuthTokenDto]

  }

  object AuthJsonProtocol extends AuthJsonProtocol

}
