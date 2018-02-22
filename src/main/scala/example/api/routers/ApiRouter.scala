package example.api.routers

import example.api.protocol.{ ApiJsonProtocol, ApiResponse }
import example.services.{ AccountService, AuthService }

class ApiRouter(
  accountService: AccountService,
  authService:    AuthService
) {

  val accountApiRouter = new AccountApiRouter(accountService)
  val authApiRouter = new AuthApiRouter(authService)

  import ApiJsonProtocol._

  // format: OFF
  val routes: Route = handleRejections(ValidationRejectionHandler) {
    sessionFromAuthorizationHeader { implicit optionalSession ⇒
      pathPrefix("api" / "v1") {
        healthRouter ~
          accountApiRouter.route ~
          authApiRouter.route
      }
    }
  }
  // format: ON

  def healthRouter: Route = pathPrefix("health") {
    get {
      complete(ApiResponse.OK)
    }
  }

}
