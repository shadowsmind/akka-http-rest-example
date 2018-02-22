package example.api.directives

import akka.http.scaladsl.server.AuthorizationFailedRejection
import example.api.RoutingSpec
import example.common.config.ConfigKeeper
import example.common.domain.UserRole
import example.common.domain.auth.{ AuthJsonProtocol, UserSession }
import example.common.utils.JwtHelper

class AuthDirectivesSpec extends RoutingSpec {

  "AuthDirectives" when {
    "provideSession" should {
      "provide UserSession == None if token is None" in {
        val token: Option[String] = None

        Get("any") ~> {
          AuthDirectives.provideSession(token) { session ⇒
            assert(session.isEmpty)
            completeOk
          }
        } ~> check {
          response shouldEqual Ok
        }
      }

      "provide UserSession == None if token is wrong" in {
        val token = Some("asfs0f92f02f20f20")

        Get("any") ~> {
          AuthDirectives.provideSession(token) { session ⇒
            assert(session.isEmpty)
            completeOk
          }
        } ~> check {
          response shouldEqual Ok
        }
      }

      "provide same UserSession for encoded token from this session" in {
        import AuthJsonProtocol._

        val userSession = UserSession(accountId = 1)
        val userSessionJson = toJson[UserSession](userSession).compactPrint
        val token = JwtHelper.encode(userSessionJson, ConfigKeeper.appConfig.security.tokenSecret)

        Get("any") ~> {
          AuthDirectives.provideSession(Some(token)) { session ⇒
            assert(session.contains(userSession))
            completeOk
          }
        } ~> check {
          response shouldEqual Ok
        }
      }
    }

    "sesionFromAuthorizationHeader" should {
      "provide UserSession == None if no Authorization header in request" in {
        Get("any") ~> {
          AuthDirectives.sessionFromAuthorizationHeader { session ⇒
            assert(session.isEmpty)
            completeOk
          }
        } ~> check {
          response shouldEqual Ok
        }
      }

      "provide UserSession == None if token from Authorization header invalid" in {
        val token = "fasdf20f9sdff"

        Get("any") ~> addHeader("Authorization", s"Bearer $token") ~> {
          AuthDirectives.sessionFromAuthorizationHeader { session ⇒
            assert(session.isEmpty)
            completeOk
          }
        } ~> check {
          response shouldEqual Ok
        }
      }

      "provide UserSession from header token" in {
        import AuthJsonProtocol._

        val userSession = UserSession(accountId = 1)
        val userSessionJson = toJson[UserSession](userSession).compactPrint
        val token = JwtHelper.encode(userSessionJson, ConfigKeeper.appConfig.security.tokenSecret)

        Get("any") ~> addHeader("Authorization", s"Bearer $token") ~> {
          AuthDirectives.sessionFromAuthorizationHeader { session ⇒
            assert(session.contains(userSession))
            completeOk
          }
        } ~> check {
          response shouldEqual Ok
        }
      }
    }

    "sessionFromUrlParamToken" should {
      "provide UserSession == None if url param `auth_token` not selected" in {
        Get("any") ~> {
          AuthDirectives.sessionFromUtlParamToken { session ⇒
            assert(session.isEmpty)
            completeOk
          }
        } ~> check {
          response shouldEqual Ok
        }
      }

      "provide UserSession == None if url param `auth_token` is invalid" in {
        val token = "fasdf20f9sdff"

        Get(s"any?auth_token=$token") ~> {
          AuthDirectives.sessionFromUtlParamToken { session ⇒
            assert(session.isEmpty)
            completeOk
          }
        } ~> check {
          response shouldEqual Ok
        }
      }

      "provide UserSession from url param auth_token" in {
        import AuthJsonProtocol._

        val userSession = UserSession(accountId = 1)
        val userSessionJson = toJson[UserSession](userSession).compactPrint
        val token = JwtHelper.encode(userSessionJson, ConfigKeeper.appConfig.security.tokenSecret)

        Get(s"any?auth_token=$token") ~> {
          AuthDirectives.sessionFromUtlParamToken { session ⇒
            assert(session.contains(userSession))
            completeOk
          }
        } ~> check {
          response shouldEqual Ok
        }
      }
    }

    "authorized" should {
      "reject with AuthorizationFailedRejection if UserSession == None" in {
        Get("any") ~> {
          AuthDirectives.authorized(None) { _ ⇒
            completeOk
          }
        } ~> check {
          rejection shouldEqual AuthorizationFailedRejection
        }
      }

      "provide UserSession from Option[UserSession]" in {
        val userSession = UserSession(accountId = 1)

        Get("any") ~> {
          AuthDirectives.authorized(Some(userSession)) { session ⇒
            assert(userSession == session)
            completeOk
          }
        } ~> check {
          response shouldEqual Ok
        }
      }
    }

    "admin" should {
      "reject with AuthorizationFailedRejection if UserSession == None" in {
        Get("any") ~> {
          AuthDirectives.admin(None) { _ ⇒
            completeOk
          }
        } ~> check {
          rejection shouldEqual AuthorizationFailedRejection
        }
      }

      "reject with AuthorizationFailedRejection if UserSession role not Admin" in {
        val userSession = UserSession(accountId = 1, role = UserRole.User)

        Get("any") ~> {
          AuthDirectives.admin(Some(userSession)) { _ ⇒
            completeOk
          }
        } ~> check {
          rejection shouldEqual AuthorizationFailedRejection
        }
      }

      "provide UserSession if is Admin" in {
        val userSession = UserSession(accountId = 1, role = UserRole.Admin)

        Get("any") ~> {
          AuthDirectives.admin(Some(userSession)) { session ⇒
            assert(userSession == session)
            assert(session.isAdmin)
            completeOk
          }
        } ~> check {
          response shouldEqual Ok
        }
      }
    }
  }

}
