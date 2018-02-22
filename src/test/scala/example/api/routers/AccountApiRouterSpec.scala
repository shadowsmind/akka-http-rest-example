package example.api.routers

import akka.http.scaladsl.model.{ HttpEntity, MediaTypes, StatusCodes }
import akka.util.ByteString
import example.api.RoutingSpec
import example.common.domain.UserRole
import example.common.domain.accounts._
import example.common.domain.auth.UserSession
import example.services.{ AccountService, AsyncServiceAction, AsyncServiceResult, ServiceResult }
import example.common.utils.DateHelper

class AccountApiRouterSpec extends RoutingSpec {

  val accountService = new AccountService {
    override def register(dto: RegisterDto): AsyncServiceAction =
      ServiceResult.action
    override def update(id: Long, dto: AccountUpdateDto, session: UserSession): AsyncServiceAction =
      ServiceResult.action

    override def getOne(id: Long, session: Option[UserSession]): AsyncServiceResult[Account] =
      ServiceResult.result(Account(id       = Some(id), nickName = "testUser", email = "test@test.ru", password = "asdf1234"))
  }

  val routes = handleRejections(ValidationRejectionHandler) {
    val sessionMocked = UserSession(1, UserRole.User, DateHelper.nowTimestamp, DateHelper.nowTimestamp + (30 * 24 * 60 * 60 * 1000))
    new AccountApiRouter(accountService).route(Some(sessionMocked))
  }

  "AccountApiRouter" should {

    import AccountsJsonProtocol._

    "return right response for registration router [Post /accounts] with valid body data" in {
      val registerDto = RegisterDto(nickName       = "test", email = "test@test.ru", password = "asdf1234", passwordRepeat = "asdf1234")
      val registerDtoJson = toJson(registerDto).compactPrint

      Post("/accounts", HttpEntity(MediaTypes.`application/json`, ByteString(registerDtoJson))) ~> routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual """{"status":200}"""
      }
    }

  }

}
