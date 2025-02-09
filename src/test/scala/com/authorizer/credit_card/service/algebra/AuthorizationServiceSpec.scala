package com.authorizer.credit_card.service.algebra

import java.time.{ZonedDateTime, ZoneId}

import cats.Id
import com.authorizer.account.adt.{Account, CreditCardTransaction}
import com.authorizer.account.violation.Violation
import com.authorizer.credit_card.adt.CreditCardAccount
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

case class AuthorizationServiceSpec() extends AsyncWordSpec with Matchers {

  val zoneId = ZoneId.of("UTC")
  val now: ZonedDateTime = ZonedDateTime.now()

  val fixtures = new {
    val accounts: List[CreditCardAccount] = List(CreditCardAccount(activeCard = true, availableLimit = 200))
    val transactions: List[CreditCardTransaction] = List(
      CreditCardTransaction("Submarino", 10, ZonedDateTime.of(2020, 2, 4, 12, 25, 0, 0, zoneId)),
      CreditCardTransaction("Burger King", 30, ZonedDateTime.of(2020, 2, 4, 12, 30, 0, 0, zoneId)),
      CreditCardTransaction("Amazon", 10, ZonedDateTime.of(2020, 2, 4, 12, 35, 0, 0, zoneId)),
      CreditCardTransaction("DigitalOcean", 70, ZonedDateTime.of(2020, 2, 4, 12, 40, 0, 0, zoneId)),
      CreditCardTransaction("C&A", 80, ZonedDateTime.of(2020, 2, 4, 12, 45, 0, 0, zoneId)))
    val accountService: AccountService[Id] = AccountService()
    val service: AuthorizationService[Id] = AuthorizationService(accountService)
  }

  "AuthorizationService" should {
    "return a succeeded transactions results" when {
      "processTransaction function has been called with valid account and transactions" in {
        val account = fixtures.accounts.head
        val transactions = fixtures.transactions
        val service = fixtures.service

        val results = service.processTransactions(account, transactions, Set.empty[Violation])

        val result1 = results(0)
        val result2 = results(1)
        val result3 = results(2)
        val result4 = results(3)
        val result5 = results(4)

        assert(results.length === 5)

        assert(result1.account.get.activeCard === true)
        assert(result1.account.get.availableLimit === BigDecimal("190"))
        assert(result1.violations === Set.empty[Violation])

        assert(result2.account.get.activeCard === true)
        assert(result2.account.get.availableLimit === BigDecimal("160"))
        assert(result2.violations === Set.empty[Violation])

        assert(result3.account.get.activeCard === true)
        assert(result3.account.get.availableLimit === BigDecimal("150"))
        assert(result3.violations === Set.empty[Violation])

        assert(result4.account.get.activeCard === true)
        assert(result4.account.get.availableLimit === BigDecimal("80"))
        assert(result4.violations === Set.empty[Violation])

        assert(result5.account.get.activeCard === true)
        assert(result5.account.get.availableLimit === BigDecimal("0"))
        assert(result5.violations === Set.empty[Violation])
      }
    }

    "return a some succeeded and some not succeeded transactions result" when {
      "processTransaction function has been called with valid account but without all necessary amount" in {
        val account = CreditCardAccount(activeCard = true, availableLimit = 110)
        val transactions = fixtures.transactions
        val service = fixtures.service

        val results = service.processTransactions(account, transactions, Set.empty[Violation])

        val result1 = results(0)
        val result2 = results(1)
        val result3 = results(2)
        val result4 = results(3)
        val result5 = results(4)

        assert(results.length === 5)

        assert(result1.account.get.activeCard === true)
        assert(result1.account.get.availableLimit === BigDecimal("100"))
        assert(result1.violations === Set.empty[Violation])

        assert(result2.account.get.activeCard === true)
        assert(result2.account.get.availableLimit === BigDecimal("70"))
        assert(result2.violations === Set.empty[Violation])

        assert(result3.account.get.activeCard === true)
        assert(result3.account.get.availableLimit === BigDecimal("60"))
        assert(result3.violations === Set.empty[Violation])

        assert(result4.account.get.activeCard === true)
        assert(result4.account.get.availableLimit === BigDecimal("60"))
        assert(result4.violations === Set(Violation("insufficient-limit")))

        assert(result5.account.get.activeCard === true)
        assert(result5.account.get.availableLimit === BigDecimal("60"))
        assert(result5.violations === Set(Violation("insufficient-limit")))
      }
    }

    "create account" when {
      "create function has been called with single one account list" in {
        val service = fixtures.service

        val result = service.create(fixtures.accounts)

        assert(result.account.get.activeCard === true)
        assert(result.account.get.availableLimit === BigDecimal("200"))
        assert(result.violations === Set.empty[Violation])
      }
    }

    "create account and return an error" when {
      "create function has been called with two accounts on list" in {
        val service = fixtures.service

        val result = service.create(fixtures.accounts :+ CreditCardAccount(activeCard = true, BigDecimal("5000")))

        assert(result.account.get.activeCard === true)
        assert(result.account.get.availableLimit === BigDecimal("200"))
        assert(result.violations === Set(Violation("account-already-initialized")))
      }
    }

    "authorize transactions" when {
      "authorize function has been called with valid account and transactions" in {
        val accounts = fixtures.accounts
        val transactions = fixtures.transactions
        val service = fixtures.service

        val results = service.authorize(accounts, transactions)

        val result1 = results(0)
        val result2 = results(1)
        val result3 = results(2)
        val result4 = results(3)
        val result5 = results(4)

        assert(results.length === 5)

        assert(result1.account.get.activeCard === true)
        assert(result1.account.get.availableLimit === BigDecimal("190"))
        assert(result1.violations === Set.empty[Violation])

        assert(result2.account.get.activeCard === true)
        assert(result2.account.get.availableLimit === BigDecimal("160"))
        assert(result2.violations === Set.empty[Violation])

        assert(result3.account.get.activeCard === true)
        assert(result3.account.get.availableLimit === BigDecimal("150"))
        assert(result3.violations === Set.empty[Violation])

        assert(result4.account.get.activeCard === true)
        assert(result4.account.get.availableLimit === BigDecimal("80"))
        assert(result4.violations === Set.empty[Violation])

        assert(result5.account.get.activeCard === true)
        assert(result5.account.get.availableLimit === BigDecimal("0"))
        assert(result5.violations === Set.empty[Violation])
      }
    }

    "reject transactions" when {
      "valid account not found" in {
        val accounts = List.empty[Account]
        val transactions = fixtures.transactions
        val service = fixtures.service

        val results = service.authorize(accounts, transactions)
        assert(results.head.violations === Set(Violation("account-not-initialized")))
      }

      "doubled transactions" in {
        val accounts = fixtures.accounts

        val transactions = List(
          CreditCardTransaction("Amazon", 10, ZonedDateTime.of(2020, 2, 4, 12, 20, 0, 0, zoneId)),
          CreditCardTransaction("Amazon", 10, ZonedDateTime.of(2020, 2, 4, 12, 21, 0, 0, zoneId)),
          CreditCardTransaction("Burger King", 30, ZonedDateTime.of(2020, 2, 4, 12, 25, 0, 0, zoneId))
        )
        val service = fixtures.service

        val results = service.authorize(accounts, transactions)
        val result1 = results(0)
        val result2 = results(1)
        val result3 = results(2)

        assert(results.length === 3)

        assert(result1.account.get.activeCard === true)
        assert(result1.account.get.availableLimit === BigDecimal("190"))
        assert(result1.violations === Set.empty[Violation])

        assert(result2.account.get.activeCard === true)
        assert(result2.account.get.availableLimit === BigDecimal("190"))
        assert(result2.violations === Set(Violation("doubled-transaction")))

        assert(result3.account.get.activeCard === true)
        assert(result3.account.get.availableLimit === BigDecimal("160"))
        assert(result3.violations === Set.empty[Violation])
      }

      "high frequency with small interval transactions" in {
        val accounts = fixtures.accounts

        val transactions = List(
          CreditCardTransaction("DigitalOcean", 80, ZonedDateTime.of(2020, 2, 4, 12, 20, 0, 0, zoneId)),
          CreditCardTransaction("Amazon", 30, ZonedDateTime.of(2020, 2, 4, 12, 21, 0, 0, zoneId)),
          CreditCardTransaction("Burger King", 25, ZonedDateTime.of(2020, 2, 4, 12, 21, 0, 0, zoneId)),
          CreditCardTransaction("C&A", 5, ZonedDateTime.of(2020, 2, 4, 12, 21, 0, 0, zoneId))
        )
        val service = fixtures.service

        val results = service.authorize(accounts, transactions)
        val result1 = results(0)
        val result2 = results(1)
        val result3 = results(2)
        val result4 = results(3)

        assert(results.length === 4)

        assert(result1.account.get.activeCard === true)
        assert(result1.account.get.availableLimit === BigDecimal("120"))
        assert(result1.violations === Set.empty[Violation])

        assert(result2.account.get.activeCard === true)
        assert(result2.account.get.availableLimit === BigDecimal("90"))
        assert(result2.violations === Set.empty[Violation])

        assert(result3.account.get.activeCard === true)
        assert(result3.account.get.availableLimit === BigDecimal("65"))
        assert(result3.violations === Set.empty[Violation])

        assert(result4.account.get.activeCard === true)
        assert(result4.account.get.availableLimit === BigDecimal("65"))
        assert(result4.violations === Set(Violation("high-frequency-small-interval")))
      }
    }
  }
}
