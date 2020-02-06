package com.authorizer.cli

import com.authorizer.account.violation.Violation
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

case class AuthorizerCLISpec() extends AsyncWordSpec with Matchers {

  val fixtures = new {
    val testScenario1 = "resources/input/test_scenario_1.txt"
    val testScenario2 = "resources/input/test_scenario_2.txt"
    val testScenario3 = "resources/input/test_scenario_3.txt"
    val testScenario4 = "resources/input/test_scenario_4.txt"
    val testScenario5 = "resources/input/test_scenario_5.txt"
    val testScenario6 = "resources/input/test_scenario_6.txt"
    val testScenario7 = "resources/input/test_scenario_7.txt"
    val testScenario8 = "resources/input/test_scenario_8.txt"
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

        val result1 = result.head

        assert(result.length === 1)

        assert(result1.account === None)
        assert(result1.violations === Set(Violation("account-not-initialized").toString))
      }

      "receives more than one account" in {
        val result = AuthorizerCLI.authorize(fixtures.testScenario3)

        val result1 = result(0)
        val result2 = result(1)
        val result3 = result(2)

        assert(result.length === 3)

        assert(result1.account.get.activeCard === true)
        assert(result1.account.get.availableLimit === BigDecimal("100"))
        assert(result1.violations === Set(Violation("account-already-initialized").toString))

        assert(result2.account.get.activeCard === true)
        assert(result2.account.get.availableLimit === BigDecimal("60"))
        assert(result2.violations === Set.empty[Violation])

        assert(result3.account.get.activeCard === true)
        assert(result3.account.get.availableLimit === BigDecimal("20"))
        assert(result3.violations === Set.empty[Violation])
      }

      "try to process transaction when account has no limit" in {
        val result = AuthorizerCLI.authorize(fixtures.testScenario5)

        val result1 = result.head

        assert(result.length === 1)

        assert(result1.account.get.activeCard === false)
        assert(result1.account.get.availableLimit === BigDecimal("100"))
        assert(result1.violations === Set(Violation("card-not-active").toString))
      }

      "receives high frequency transactions in a small interval" in {
        val result = AuthorizerCLI.authorize(fixtures.testScenario6)

        val result1 = result(0)
        val result2 = result(1)
        val result3 = result(2)
        val result4 = result(3)

        assert(result.length === 4)

        assert(result1.account.get.activeCard === true)
        assert(result1.account.get.availableLimit === BigDecimal("80"))
        assert(result1.violations === Set.empty[Violation])

        assert(result2.account.get.activeCard === true)
        assert(result2.account.get.availableLimit === BigDecimal("40"))
        assert(result2.violations === Set.empty[Violation])

        assert(result3.account.get.activeCard === true)
        assert(result3.account.get.availableLimit === BigDecimal("10"))
        assert(result3.violations === Set.empty[Violation])

        assert(result4.account.get.activeCard === true)
        assert(result4.account.get.availableLimit === BigDecimal("10"))
        assert(result4.violations === Set(Violation("high-frequency-small-interval").toString))
      }

      "receives doubled transaction" in {
        val result = AuthorizerCLI.authorize(fixtures.testScenario7)

        val result1 = result(0)
        val result2 = result(1)
        val result3 = result(2)

        assert(result.length === 3)

        assert(result1.account.get.activeCard === true)
        assert(result1.account.get.availableLimit === BigDecimal("80"))
        assert(result1.violations === Set.empty[Violation])

        assert(result2.account.get.activeCard === true)
        assert(result2.account.get.availableLimit === BigDecimal("80"))
        assert(result2.violations === Set(Violation("doubled-transaction").toString))

        assert(result3.account.get.activeCard === true)
        assert(result3.account.get.availableLimit === BigDecimal("40"))
        assert(result3.violations === Set.empty[Violation])
      }

      "receives multiple violations (high frequency small interval and insufficient limit)" in {
        val result = AuthorizerCLI.authorize(fixtures.testScenario8)

        val result1 = result(0)
        val result2 = result(1)
        val result3 = result(2)
        val result4 = result(3)
        val result5 = result(4)

        assert(result.length === 5)

        assert(result1.account.get.activeCard === true)
        assert(result1.account.get.availableLimit === BigDecimal("80"))
        assert(result1.violations === Set.empty[Violation])

        assert(result2.account.get.activeCard === true)
        assert(result2.account.get.availableLimit === BigDecimal("40"))
        assert(result2.violations === Set.empty[Violation])

        assert(result3.account.get.activeCard === true)
        assert(result3.account.get.availableLimit === BigDecimal("10"))
        assert(result3.violations === Set.empty[Violation])

        assert(result4.account.get.activeCard === true)
        assert(result4.account.get.availableLimit === BigDecimal("10"))
        assert(result4.violations === Set(Violation("high-frequency-small-interval").toString))

        assert(result5.account.get.activeCard === true)
        assert(result5.account.get.availableLimit === BigDecimal("10"))
        assert(result5.violations === Set(Violation("insufficient-limit").toString))
      }
    }
  }
}
