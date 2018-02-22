package example.api.directives

import akka.http.scaladsl.model.headers.Authorization
import akka.http.scaladsl.server._, Directives._
import example.common.config.ConfigKeeper
import example.common.domain.auth.{ AuthJsonProtocol, UserSession }
import example.common.utils.JwtHelper

import scala.util.{ Failure, Success }

trait AuthDirectives {

  private val config = ConfigKeeper.appConfig.security

  import AuthJsonProtocol._

  def provideSession(optionalToken: Option[String]): Directive1[Option[UserSession]] = {
    optionalToken match {
      case Some(token) ⇒
        JwtHelper.decode(token, config.tokenSecret) match {
          case Success(content) ⇒
            val userSession = fromJson[UserSession](content)
            provide(Some(userSession))

          case Failure(e) ⇒
            provide(None)
        }

      case None ⇒ provide(None)
    }
  }

  def sessionFromAuthorizationHeader: Directive1[Option[UserSession]] = {

    def fromAuthHeader(header: Option[Authorization]): Directive1[Option[UserSession]] =
      provideSession(header.map(_.credentials.token))

    optionalHeaderValueByType[Authorization]()
      .flatMap(fromAuthHeader)
  }

  def sessionFromUtlParamToken: Directive1[Option[UserSession]] = {
    parameter('auth_token.?)
      .flatMap(provideSession)
  }

  def authorized(optionalSession: Option[UserSession]): Directive1[UserSession] = {
    optionalSession match {
      case Some(session) ⇒
        provide(session)

      case None ⇒
        reject(AuthorizationFailedRejection)
    }
  }

  def admin(optionalSession: Option[UserSession]): Directive1[UserSession] = {
    optionalSession match {
      case Some(session) ⇒
        if (session.isAdmin) {
          provide(session)
        } else {
          reject(AuthorizationFailedRejection)
        }

      case None ⇒ reject(AuthorizationFailedRejection)
    }
  }

}

object AuthDirectives extends AuthDirectives
