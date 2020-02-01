package com.authorizer.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class AccountSpec extends AsyncWordSpec with Matchers {

  val fixture = new {
    val account: CreditAccount = CreditAccount(activeCard = true, availableLimit = 100.0)
  }

  "CreditAccount object" should {
    "have same values as the account defined fixture" in {
      val creditAccount = fixture.account

      assert(creditAccount.activeCard === true)
      assert(creditAccount.availableLimit === BigDecimal("100.0"))
    }
  }

}
