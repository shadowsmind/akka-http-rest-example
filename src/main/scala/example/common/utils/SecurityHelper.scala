package example.common.utils

import com.github.t3hnar.bcrypt._

object SecurityHelper {

  def hashPassword(password: String): String = {
    password.bcrypt
  }

  def passwordMatch(password: String, hash: String): Boolean = {
    password.isBcrypted(hash)
  }

}
