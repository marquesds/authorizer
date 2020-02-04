package com.authorizer.credit_card.service.algebra

import cats.Monad
import com.authorizer.account.adt.{Account, AuthorizationResult, Transaction}
import com.authorizer.account.service.algebra.AccountServiceAlgebra
import com.authorizer.account.violation.Violation
import com.authorizer.credit_card.adt.CreditCardAccount

import scala.math.BigDecimal

case class AccountService[F[_] : Monad]() extends AccountServiceAlgebra[F, CreditCardAccount, Unit] {

  def process(account: CreditCardAccount, transaction: Transaction, violations: Set[Violation] = Set.empty): F[AuthorizationResult] = {
    val subtractResult: Either[Violation, Account] = subtract(account, transaction.amount)

    val composedViolations: Set[Violation] = violations ++
      CreditCardRule.cardActiveRule(isCardActive(account)) ++
      CreditCardRule.availableLimitRule(subtractResult)

    val accountWithNewValue = if (composedViolations.isEmpty) subtractResult.getOrElse(account) else account
    Monad[F].pure(AuthorizationResult(Some(accountWithNewValue), composedViolations))
  }

  def subtract(account: CreditCardAccount, amount: BigDecimal): Either[Violation, Account] = {
    val result = account.availableLimit - amount
    if (result >= 0) Right(account.copy(availableLimit = result)) else Left(Violation("insufficient-limit"))
  }

  def isCardActive(account: CreditCardAccount): Boolean = account.activeCard
}

object CreditCardRule {
  def cardActiveRule(isCardActive: Boolean): Option[Violation] = if (!isCardActive) Some(Violation("card-not-active")) else None

  def availableLimitRule(subtractResult: Either[Violation, Account]): Option[Violation] = {
    subtractResult match {
      case Left(violation) => Some(violation)
      case _ => None
    }
  }
}