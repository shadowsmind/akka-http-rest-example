package example.api.routers

import akka.http.scaladsl.model.{ HttpEntity, MediaTypes, StatusCodes }
import akka.util.ByteString
import org.scalatest.AsyncWordSpec
import example.api.GenericRoutingSpec
import example.api.protocol.ApiResponse
import example.common.domain.accounts.Account
import example.common.domain.auth._
import example.services.{ AsyncServiceResult, AuthService, ServiceResult }

class AuthApiRouterSpec extends AsyncWordSpec with GenericRoutingSpec {

  val authServiceMocked = new AuthService {

    val account = Account(id       = Some(1), nickName = "test", email = "test@test.ru", password = "asdf1234")
    val authToken = encodeToken(account)

    override def auth(dto: AuthViaPasswordDto): AsyncServiceResult[AuthToken] =
      ServiceResult.result(authToken)

  }

  val routes = handleRejections(ValidationRejectionHandler) {
    new AuthApiRouter(authServiceMocked).route
  }

  "AuthApiRouter" when {

    import AuthJsonProtocol._

    "POST /api/auth" should {

      "return right response" in {
        val authDto = AuthViaPasswordDto("test", "asdf1234")
        val authDtoJson = toJson(authDto).compactPrint

        authServiceMocked.auth(authDto).map { result ⇒
          val validResponse = toJson(ApiResponse.convert(result, AuthTokenDto.fromToken)._2).compactPrint

          Post("/auth", HttpEntity(MediaTypes.`application/json`, authDtoJson)) ~> routes ~> check {
            status shouldEqual StatusCodes.OK
            responseAs[String] shouldEqual validResponse
          }
        }
      }

    }

  }

}
