package example.common.utils

import org.scalatest.{ Matchers, WordSpec }

class DateHelperSpec extends WordSpec with Matchers {

  "DateHelper" must {

    "return true for method isPassed if date with duration is passed" in {
      val expirySeconds = 10

      val timeAfter = DateHelper.now.plusSeconds(30)

      assert(DateHelper.isPassed(timeAfter, expirySeconds))
    }

    "return false for method isPassed if date with duration is not passed" in {
      val expirySeconds = 10

      val timeBefore = DateHelper.now.minusSeconds(30)

      assert(!DateHelper.isPassed(timeBefore, expirySeconds))
    }

  }

}
