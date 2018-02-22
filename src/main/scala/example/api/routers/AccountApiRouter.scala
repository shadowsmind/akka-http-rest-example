package example.api.routers

import example.api.protocol.ApiResponse
import example.common.domain.accounts._
import example.common.domain.auth.UserSession
import example.services.AccountService
import example.validation.RegisterValidator

class AccountApiRouter(accountService: AccountService) {

  import AccountsJsonProtocol._

  // format: OFF
  def route(implicit session: Option[UserSession]): Route = pathPrefix("accounts") {
    registerRouter ~
    updateRouter ~
    getOneRouter
  }
  // format: ON

  private def registerRouter: Route =
    validPostJson(as[RegisterDto], RegisterValidator.validate) { dto ⇒
      onSuccess(accountService.register(dto)) { result ⇒
        complete(ApiResponse.convert(result))
      }
    }

  private def updateRouter(implicit session: Option[UserSession]): Route = path(LongNumber) { id ⇒
    postJson(as[AccountUpdateDto]) { dto ⇒
      authorized(session) { session ⇒
        onSuccess(accountService.update(id, dto, session)) { result ⇒
          complete(ApiResponse.convert(result))
        }
      }
    }
  }

  private def getOneRouter(implicit session: Option[UserSession]): Route = path(LongNumber) { id ⇒
    get {
      onSuccess(accountService.getOne(id, session)) { result ⇒
        complete(ApiResponse.convert(result, AccountConverter.toDto))
      }
    }
  }

}
