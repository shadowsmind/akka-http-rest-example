package example.validation

object IdValidator {

  def validate(data: Long, field: String = "id"): ValidationResult[Long] = {
    if (data <= 0) {
      (field, "{invalid}").invalidNel
    } else {
      data.validNel
    }
  }

}
