package com.authorizer.credit_card.adt

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class AccountSpec extends AsyncWordSpec with Matchers {

  val fixtures = new {
    val account: CreditCardAccount = CreditCardAccount(activeCard = true, availableLimit = 100.0)
  }

  "CreditCardAccount object" should {
    "have same values as the account defined fixture" in {
      val creditAccount = fixtures.account

      assert(creditAccount.activeCard === true)
      assert(creditAccount.availableLimit === BigDecimal("100.0"))
    }
  }

}
