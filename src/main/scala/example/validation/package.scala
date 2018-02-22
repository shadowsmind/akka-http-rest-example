package example

import cats.data.{ NonEmptyList, ValidatedNel }
import cats.syntax.{ ApplySyntax, ValidatedSyntax }
import example.services.ErrorDetails

import scala.language.implicitConversions
import scala.util.matching.Regex

package object validation extends ValidatedSyntax with ApplySyntax {

  type ValidationError = (String, String)
  type ValidationErrors = NonEmptyList[ValidationError]
  type ValidationResult[T] = ValidatedNel[ValidationError, T]

  def notEmpty(source: String, field: String): ValidationResult[String] = {
    if (source.isEmpty)
      (field, "{must_non_be_empty}").invalidNel
    else
      source.validNel
  }

  def checkRegex(regex: Regex, source: String, field: String, error: String = "{wrong_value}"): ValidationResult[String] = {
    if (regex.findAllIn(source).isEmpty) {
      (field, error).invalidNel
    } else {
      source.validNel
    }
  }

  object ValidationHelper {

    def convertErrors(errors: ValidationErrors): ErrorDetails = {
      Some(errors.tail.toMap + errors.head)
    }

  }

}
