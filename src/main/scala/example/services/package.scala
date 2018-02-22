package example

import akka.http.scaladsl.util.FastFuture
import cats.data.Validated.{ Invalid, Valid }
import example.validation.{ ValidationErrors, ValidationHelper, ValidationResult }

import scala.concurrent.{ ExecutionContextExecutor, Future }

package object services {

  val Done = Right(())

  type ErrorDetails = Option[Map[String, String]]

  case class ServiceError(code: Int, message: String, details: ErrorDetails)

  type ServiceResult[T] = Either[ServiceError, T]
  type AsyncServiceResult[T] = Future[ServiceResult[T]]
  type AsyncServiceAction = AsyncServiceResult[Unit]

  object ServiceResult {

    def apply[T](f: ⇒ T): AsyncServiceResult[T] = result(f)

    def action: AsyncServiceResult[Unit] =
      FastFuture.successful(Done)

    def result[T](value: T): AsyncServiceResult[T] =
      FastFuture.successful(Right(value))

    def orNotFound[T](value: Option[T]): AsyncServiceResult[T] =
      FastFuture.successful {
        value match {
          case Some(v) ⇒ Right(v)
          case None    ⇒ Left(ServiceError(404, "{not_found}", None))
        }
      }

    def error[T](error: ServiceError): AsyncServiceResult[T] =
      FastFuture.successful(Left(error))

    def error[T](code: Int, message: String, details: ErrorDetails = None): AsyncServiceResult[T] =
      error(ServiceError(code, message, details))

    def validationError[T](errors: ValidationErrors): AsyncServiceResult[T] =
      error(409, "{validation_error}", ValidationHelper.convertErrors(errors))

  }

  implicit class FutureAsResult[T](future: Future[T]) {

    def asResult(implicit ex: ExecutionContextExecutor): AsyncServiceResult[T] =
      future.map(Right.apply)

    def asAction(implicit ex: ExecutionContextExecutor): AsyncServiceResult[Unit] =
      future.map(_ ⇒ Done)

    def asActionWith(callback: ⇒ Any)(implicit ex: ExecutionContextExecutor): AsyncServiceResult[Unit] =
      future.map { _ ⇒ callback; Done }

    def onValid[D, R](validation: T ⇒ ValidationResult[D])(f: D ⇒ AsyncServiceResult[R])(
      implicit
      ex: ExecutionContextExecutor
    ): AsyncServiceResult[R] = {
      future
        .map(validation)
        .flatMap {
          case Valid(data)     ⇒ f(data)
          case Invalid(errors) ⇒ ServiceResult.validationError(errors)
        }
    }

  }

}
