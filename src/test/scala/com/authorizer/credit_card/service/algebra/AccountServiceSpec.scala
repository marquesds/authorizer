package com.authorizer.credit_card.service.algebra

import cats.Id
import com.authorizer.account.violation.{Unknown, Violation}
import com.authorizer.credit_card.adt.CreditCardAccount
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class AccountServiceSpec extends AsyncWordSpec with Matchers {

  val fixtures = new {
    val account: CreditCardAccount = CreditCardAccount(activeCard = true, availableLimit = 100)
    val service: AccountService[Id] = AccountService()
  }

  "AccountService" should {
    "return an account with new limit" when {
      "subtract a transaction amount from account.availableLimit" in {
        val account = fixtures.account
        val service = fixtures.service

        val result = service.subtract(account, 90)
        assert(result.getOrElse(account).availableLimit === BigDecimal("10"))
      }
    }

    "return an insufficient-limit violation" when {
      "try to subtract a greater transaction amount from a lower account.availableLimit" in {
        val account = fixtures.account
        val service = fixtures.service

        val result = service.subtract(account, 120)
        assert(result.left.getOrElse(Unknown) === Violation("insufficient-limit"))
      }
    }
  }

}
