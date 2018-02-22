package example.api.protocol

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import example.common.domain.{ EntityStatus, UserRole }

trait ApiJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {

  def apiResponseFormat[T](implicit format: RootJsonFormat[T]): RootJsonFormat[ApiResponseData[T]] =
    jsonFormat2(ApiResponseData[T])

  def toJson[T](data: T)(implicit format: JsonFormat[T]): JsValue =
    data.toJson

  def fromJson[T](data: String)(implicit format: JsonFormat[T]): T =
    format.read(JsonParser(data))

  implicit val entityStatusFormat = EnumJsonFormat(EntityStatus)
  implicit val userRoleFormat = EnumJsonFormat(UserRole)

  implicit val errorDtoFormat = jsonFormat2(ErrorDto)
  implicit val successResponseFormat = jsonFormat1(ApiResponseSuccess)
  implicit val errorResponseFormat = jsonFormat2(ApiResponseError)

}

object ApiJsonProtocol extends ApiJsonProtocol
