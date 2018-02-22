package example.api.routers

import example.api.protocol.ApiResponse
import example.common.domain.auth._
import example.services.AuthService

class AuthApiRouter(authService: AuthService) {

  import AuthJsonProtocol._

  // format: OFF
  def route: Route = pathPrefix("auth") {
    authViaPasswordRouter
  }
  // format: ON

  private def authViaPasswordRouter: Route =
    postJson(as[AuthViaPasswordDto]) { dto ⇒
      onSuccess(authService.auth(dto)) { result ⇒
        complete(ApiResponse.convert(result, AuthTokenDto.fromToken))
      }
    }

}
