package example.api.protocol

import spray.json._
import example.services.ServiceResult
import example.validation.{ ValidationErrors, ValidationHelper }

object ApiResponse {

  import ApiJsonProtocol._

  type Response = (Int, JsValue)

  val OK: Response = (200, ApiResponseSuccess().toJson)
  val BadRequest: Response = (400, ApiResponseError(400, ErrorDto()).toJson)

  def convert[T, R](result: ServiceResult[T], f: T ⇒ R)(
    implicit
    jsonFormat: RootJsonFormat[ApiResponseData[R]]
  ): Response =
    result match {
      case Right(value) ⇒
        (200, ApiResponseData(data = f(value)).toJson)

      case Left(error) ⇒
        (error.code, ApiResponseError(error.code, ErrorDto(Some(error.message), error.details)).toJson)
    }

  def convert[T](result: ServiceResult[T]): Response =
    result match {
      case Right(_) ⇒
        OK

      case Left(error) ⇒
        (error.code, ApiResponseError(error.code, ErrorDto(Some(error.message), error.details)).toJson)
    }

  def validationError(errors: ValidationErrors): Response =
    (409, ApiResponseError(409, ErrorDto(details = ValidationHelper.convertErrors(errors))).toJson)

}
