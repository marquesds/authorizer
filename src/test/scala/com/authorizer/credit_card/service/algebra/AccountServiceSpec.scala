package com.authorizer.credit_card.service.algebra

import java.time.ZonedDateTime

import cats.Id
import com.authorizer.account.adt.CreditCardTransaction
import com.authorizer.account.violation.{Unknown, Violation}
import com.authorizer.credit_card.adt.CreditCardAccount
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class AccountServiceSpec extends AsyncWordSpec with Matchers {

  val now: ZonedDateTime = ZonedDateTime.now()

  val fixtures = new {
    val account: CreditCardAccount = CreditCardAccount(activeCard = true, availableLimit = 100)
    val transaction: CreditCardTransaction = CreditCardTransaction("Burger King", 20, now)
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

    "return true" when {
      "card is active" in {
        val account = fixtures.account
        val service = fixtures.service

        val result = service.isCardActive(account)
        assert(result === true)
      }
    }

    "return false" when {
      "card is not active" in {
        val account = CreditCardAccount(activeCard = false, availableLimit = 0)
        val service = fixtures.service

        val result = service.isCardActive(account)
        assert(result === false)
      }
    }

    "return a succeeded transaction result" when {
      "process method has been called for a valid account" in {
        val account = fixtures.account
        val transaction = fixtures.transaction
        val service = fixtures.service

        val result = service.process(account, transaction, Set.empty[Violation])

        assert(result.account.get.activeCard === true)
        assert(result.account.get.availableLimit === BigDecimal("80"))
        assert(result.violations === Set.empty[Violation])
      }
    }

    "return a non succeeded transaction result" when {
      "process method has been called for an invalid account" in {
        val account = CreditCardAccount(activeCard = false, availableLimit = 0)
        val transaction = fixtures.transaction
        val service = fixtures.service

        val result = service.process(account, transaction, Set.empty[Violation])

        assert(result.account.get.activeCard === false)
        assert(result.account.get.availableLimit === BigDecimal("0"))
        assert(result.violations === Set(Violation("card-not-active"), Violation("insufficient-limit")))
      }

      "process method has been called for an account with disabled card" in {
        val account = CreditCardAccount(activeCard = false, availableLimit = 200)
        val transaction = fixtures.transaction
        val service = fixtures.service

        val result = service.process(account, transaction, Set.empty[Violation])

        assert(result.account.get.activeCard === false)
        assert(result.account.get.availableLimit === BigDecimal("200"))
        assert(result.violations === Set(Violation("card-not-active")))
      }

      "process method has been called for an account without limit" in {
        val account = CreditCardAccount(activeCard = true, availableLimit = 0)
        val transaction = fixtures.transaction
        val service = fixtures.service

        val result = service.process(account, transaction, Set.empty[Violation])

        assert(result.account.get.activeCard === true)
        assert(result.account.get.availableLimit === BigDecimal("0"))
        assert(result.violations === Set(Violation("insufficient-limit")))
      }

      "process method has been called for an account with low limit" in {
        val account = CreditCardAccount(activeCard = true, availableLimit = 15)
        val transaction = fixtures.transaction
        val service = fixtures.service

        val result = service.process(account, transaction, Set.empty[Violation])

        assert(result.account.get.activeCard === true)
        assert(result.account.get.availableLimit === BigDecimal("15"))
        assert(result.violations === Set(Violation("insufficient-limit")))
      }
    }
  }

}
