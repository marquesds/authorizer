package com.authorizer.credit_card.adt

import java.time.ZonedDateTime

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class TransactionSpec extends AsyncWordSpec with Matchers {

  val time: ZonedDateTime = ZonedDateTime.now()

  val fixtures = new {
    val transaction: CreditCardTransaction = CreditCardTransaction(merchant = "Burger King", amount = 20.0, time = time)
  }

  "CreditCardTransaction object" should {
    "have same values as the transaction defined fixture" in {
      val transaction = fixtures.transaction

      assert(transaction.merchant === "Burger King")
      assert(transaction.amount === BigDecimal("20.0"))
      assert(transaction.time === time)
    }
  }

}
