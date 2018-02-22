package example.validation

object PasswordValidator {

  def validate(password: String): ValidationResult[String] = {
    password.validNel // TODO: implement logic for validate password
  }

}
