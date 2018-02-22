package example.validation

object EmailValidator {

  def validate(email: String): ValidationResult[String] = {
    email.validNel // TODO: implement logic for validate email
  }

}
