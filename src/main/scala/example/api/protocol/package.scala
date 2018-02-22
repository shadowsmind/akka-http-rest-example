package example.api

import example.services.ErrorDetails

package object protocol {

  case class ErrorDto(
    message: Option[String] = None,
    details: ErrorDetails   = None
  )

  case class ApiResponseData[T](
    status: Int = 200,
    data:   T
  )

  case class ApiResponseSuccess(
    status: Int = 200
  )

  case class ApiResponseError(
    status: Int,
    error:  ErrorDto
  )

}
