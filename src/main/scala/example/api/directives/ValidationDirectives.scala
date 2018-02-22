package example.api.directives

import akka.http.scaladsl.server.{ Directive1, Rejection, RejectionHandler }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import cats.data.Validated.{ Invalid, Valid }
import example.api.directives.ValidationDirectives.ValidationRejection
import example.api.protocol.{ ApiJsonProtocol, ApiResponse }
import example.validation.{ ValidationErrors, ValidationResult }

trait ValidationDirectives {

  import ApiJsonProtocol._

  def validPostJson[T](um: FromRequestUnmarshaller[T], validate: T ⇒ ValidationResult[T]): Directive1[T] =
    CommonDirectives.postJson(um).flatMap { data ⇒
      validate(data) match {
        case Valid(validData) ⇒ provide(validData)
        case Invalid(errors)  ⇒ reject(ValidationRejection(errors))
      }
    }

  val ValidationRejectionHandler: RejectionHandler = RejectionHandler.newBuilder()
    .handle {
      case ValidationRejection(errors) ⇒
        complete(ApiResponse.validationError(errors))
    }
    .result()

}

object ValidationDirectives extends ValidationDirectives {

  case class ValidationRejection(errors: ValidationErrors) extends Rejection

}
