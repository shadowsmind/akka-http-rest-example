package example.common.utils

import org.scalatest.{ Matchers, WordSpec }

class SecurityHelperSpec extends WordSpec with Matchers {

  "SecurityHelper" when {

    "hashPassword and passwordMatch" should {

      "hash password with bcrypt and after check" in {
        val password = "asdf1234"
        val wrongPass = "0103jfsadf"

        val hashed = SecurityHelper.hashPassword(password)
        val otherHashed = SecurityHelper.hashPassword("pslej198323")

        assert(SecurityHelper.passwordMatch(password, hashed))
        assert(!SecurityHelper.passwordMatch(wrongPass, hashed))
        assert(!SecurityHelper.passwordMatch(password, otherHashed))
        assert(!SecurityHelper.passwordMatch(wrongPass, otherHashed))
      }

    }

  }

}
