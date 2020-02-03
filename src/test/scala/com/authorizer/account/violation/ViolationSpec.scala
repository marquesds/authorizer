package com.authorizer.account.violation

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class ViolationSpec extends AsyncWordSpec with Matchers {

  "Violation" should {
    "return AccountAlreadyInitialized object when receive an account-already-initialized value" in {
      val violation = Violation("account-already-initialized")

      assert(violation.isInstanceOf[AccountAlreadyInitialized])
      assert(violation.value === "account-already-initialized")
    }

    "return CardNotActive object when receive a card-not-active value" in {
      val violation = Violation("card-not-active")

      assert(violation.isInstanceOf[CardNotActive])
      assert(violation.value === "card-not-active")
    }

    "return InsufficientLimit object when receive an insufficient-limit value" in {
      val violation = Violation("insufficient-limit")

      assert(violation.isInstanceOf[InsufficientLimit])
      assert(violation.value === "insufficient-limit")
    }

    "return HighFrequencySmallInterval object when receive a high-frequency-small-interval value" in {
      val violation = Violation("high-frequency-small-interval")

      assert(violation.isInstanceOf[HighFrequencySmallInterval])
      assert(violation.value === "high-frequency-small-interval")
    }

    "return DoubledTransaction object when receive a doubled-transaction value" in {
      val violation = Violation("doubled-transaction")

      assert(violation.isInstanceOf[DoubledTransaction])
      assert(violation.value === "doubled-transaction")
    }

    "return Unknown object when receive an invalid value" in {
      val violation = Violation("something-else")

      assert(violation.isInstanceOf[Unknown])
      assert(violation.value === "unknown-violation")
    }
  }

}
