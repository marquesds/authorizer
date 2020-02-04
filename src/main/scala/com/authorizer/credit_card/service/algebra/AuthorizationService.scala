package com.authorizer.credit_card.service.algebra

import cats.Monad
import cats.implicits._

import com.authorizer.account.adt.{Account, AuthorizationResult, Transaction}
import com.authorizer.account.service.algebra.AuthorizationServiceAlgebra
import com.authorizer.account.violation.Violation
import com.authorizer.credit_card.adt.CreditCardAccount


case class AuthorizationService[F[_] : Monad]() extends AuthorizationServiceAlgebra[F, Unit] {

  val violations: Set[Violation] = Set.empty

  def authorize(accounts: List[Account], transactions: List[Transaction]): F[AuthorizationResult] = {
    val createAccountResult = create(accounts)
    val authorizationResult = createAccountResult.account match {
      case Some(account) =>
        val accountService = AccountService(account.asInstanceOf[CreditCardAccount])
        transactions.reduceLeft(transaction => accountService.process(transaction, createAccountResult.violations))
      case _ => createAccountResult
    }

    Monad[F].pure(authorizationResult)
  }

  def create(accounts: List[Account]): AuthorizationResult = {
    val creationViolations = Set.empty ++ AuthorizationRule.accountNotInitializedRule(accounts) ++ AuthorizationRule.accountAlreadyInitializedRule(accounts)
    if (accounts.isEmpty) AuthorizationResult(None, creationViolations) else AuthorizationResult(accounts.headOption, creationViolations)
  }
}

object AuthorizationRule {

  def accountNotInitializedRule(accounts: List[Account]): Option[Violation] =
    if (accounts.isEmpty) Some(Violation("account-not-initialized")) else None

  def accountAlreadyInitializedRule(accounts: List[Account]): Option[Violation] =
    if (accounts.length > 1) Some(Violation("account-already-initialized")) else None
}