package com.authorizer.credit_card.service.algebra

import cats.Monad
import cats.implicits._
import com.authorizer.account.adt.{Account, AuthorizationResult, Transaction}
import com.authorizer.account.service.algebra.AuthorizationServiceAlgebra
import com.authorizer.account.violation.Violation
import com.authorizer.credit_card.adt.CreditCardAccount


case class AuthorizationService[F[_] : Monad](accountService: AccountService[F]) extends AuthorizationServiceAlgebra[F, Unit] {

  val violations: Set[Violation] = Set.empty

  def authorize(accounts: List[Account], transactions: List[Transaction]): F[AuthorizationResult] = {
    val creationResult = create(accounts)

    creationResult.account match {
      case Some(account) => processTransactions(account, transactions, creationResult.violations)
      case _ => Monad[F].pure(creationResult)
    }
  }

  def create(accounts: List[Account]): AuthorizationResult = {
    val creationViolations = Set.empty ++
      AuthorizationRule.accountNotInitializedRule(accounts) ++
      AuthorizationRule.accountAlreadyInitializedRule(accounts)
    if (accounts.isEmpty) AuthorizationResult(None, creationViolations) else AuthorizationResult(accounts.headOption, creationViolations)
  }

  def processTransactions(account: Account, transactions: List[Transaction], violations: Set[Violation]): F[AuthorizationResult] = {
    if (transactions.isEmpty) {
      Monad[F].pure(AuthorizationResult(Some(account), violations))
    } else {
      accountService.process(account.asInstanceOf[CreditCardAccount], transactions.head, violations).flatMap { result =>
        result.account.map { account =>
          processTransactions(account, transactions.tail, result.violations)
        }.getOrElse(result)
      }
    }
  }
}

object AuthorizationRule {

  def accountNotInitializedRule(accounts: List[Account]): Option[Violation] =
    if (accounts.isEmpty) Some(Violation("account-not-initialized")) else None

  def accountAlreadyInitializedRule(
    accounts: List[Account]): Option[Violation] =
    if (accounts.length > 1) Some(Violation("account-already-initialized")) else None
}