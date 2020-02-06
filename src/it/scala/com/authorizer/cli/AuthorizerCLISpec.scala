package com.authorizer.cli

import com.authorizer.account.violation.Violation
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

case class AuthorizerCLISpec() extends AsyncWordSpec with Matchers {

  val fixtures = new {
    val testScenario1 = "resources/input/test_scenario_1.txt"
    val testScenario2 = "resources/input/test_scenario_2.txt"
  }

  "AuthorizerCLISpec" should {
    "process transactions with success" when {
      "receives an valid positive account with valid transactions" in {
        val result = AuthorizerCLI.authorize(fixtures.testScenario1)

        val result1 = result(0)
        val result2 = result(1)
        val result3 = result(2)

        assert(result.length === 3)

        assert(result1.account.get.activeCard === true)
        assert(result1.account.get.availableLimit === BigDecimal("80"))
        assert(result1.violations === Set.empty[Violation])

        assert(result2.account.get.activeCard === true)
        assert(result2.account.get.availableLimit === BigDecimal("40"))
        assert(result2.violations === Set.empty[Violation])

        assert(result3.account.get.activeCard === true)
        assert(result3.account.get.availableLimit === BigDecimal("0"))
        assert(result3.violations === Set.empty[Violation])
      }
    }

    "process transactions with failure" when {
      "receives valid transactions with no account" in {
        val result = AuthorizerCLI.authorize(fixtures.testScenario2)

        assert(result.length === 1)

        val result1 = result.head

        assert(result1.account === None)
        assert(result1.violations === Set(Violation("account-not-initialized").toString))
      }
    }
  }
}
